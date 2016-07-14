(ns vivavocab.games.memory.handlers
  (:require [re-frame.core :refer [register-handler]]))

(defn generate-list [state]
      (let [cards (->> state
                       :words
                       keys
                       shuffle
                       (take 6)
                       ; (1 2 3 4 5 6)
                       (mapcat (fn [word-id]
                                   (let [word-keys (->> (get-in state [:key-options])
                                                        shuffle
                                                        (take 2))]
                                        [{:word-id word-id :word-key (first word-keys)}
                                         {:word-id word-id :word-key (last word-keys)}])))
                       ; [{:word-id 1 :word-key key1}
                       ;  {:word-id 1 :word-key key2} ...]
                       shuffle
                       (map-indexed (fn [index card]
                                        {:word-id (card :word-id)
                                         :word-key (card :word-key)
                                         :status :back
                                         :index index}))
                       (reduce (fn [memo card]
                                   (assoc memo (card :index) card)) {})
                       ; {1 {:status :back
                       ;     :index 1 ...} ...}
                       )]
           cards))

(defn initialize [state]
      (assoc-in state [:game] {:cards (generate-list state)
                               :character-mood :neutral}))

(register-handler
  :memory/initialize
  (fn [state _]
      (initialize state)))

(defn flipped-cards [state]
      (->> (get-in state [:game :cards])
           vals
           (filter (fn [card]
                       (= (card :status) :front)))))

(defn cards-match? [state]
      (let [flipped-cards (flipped-cards state)]
           (= (:word-id (first flipped-cards))
              (:word-id (last flipped-cards)))))

(defn reset-character-mood [state]
      (assoc-in state [:game :character-mood] :neutral))

(defn update-character-mood [state]
      (assoc-in state [:game :character-mood] (if (= (count (flipped-cards state)) 2)
                                                (if (cards-match? state)
                                                  :happy
                                                  :angry)
                                                :neutral)))

(defn flip-card-front [state card-index]
      (-> state
          (assoc-in [:game :cards card-index :status] :front)))

(defn flip-card-back [state card-index]
      (-> state
          (assoc-in [:game :cards card-index :status] :back)))

(defn remove-card [state card-index]
      (-> state
          (assoc-in [:game :cards card-index :status] :gone)))

(defn flip-back-or-remove-cards [state]
      (let [flipped-cards (flipped-cards state)]
           (if (cards-match? state)
             (-> state
                 (remove-card (:index (first flipped-cards)))
                 (remove-card (:index (last flipped-cards))))
             (-> state
                 (flip-card-back (:index (first flipped-cards)))
                 (flip-card-back (:index (last flipped-cards)))))))

(defn check-choices [state]
           (if (= (count (flipped-cards state)) 2)
             (-> state
                 (flip-back-or-remove-cards)
                 (reset-character-mood))
             state))

(register-handler
  :memory/check-choices
  (fn [state _]
      (check-choices state)))

(register-handler
  :memory/flip-card
  (fn [state [_ card]]
      (-> state
          (check-choices)
          (flip-card-front (card :index))
          (update-character-mood))))

(register-handler
  :memory/retry
  (fn [state _]
      (initialize state)))

(register-handler
  :memory/next-level
  (fn [state _]
      (initialize state)))

(register-handler
  :memory/back-to-levels
  (fn [state _]
      ; TODO
      ))
