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
                                        (merge card
                                               {:status :back
                                                :index index})))
                       (reduce (fn [memo card]
                                   (assoc memo (card :index) card)) {})
                       ; {1 {:status :back
                       ;     :index 1 ...} ...}
                       )]
           cards))

(defn initialize [state]
      (assoc-in state [:game] {:cards (generate-list state)}))

(register-handler
  :memory/initialize
  (fn [state _]
      (initialize state)))

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
      (let [flipped-cards (->> (get-in state [:game :cards])
                               vals
                               (filter (fn [card]
                                           (= (card :status) :front))))]
      (if (= (:word-id (first flipped-cards))
             (:word-id (last flipped-cards)))
        (-> state
            (remove-card (:index (first flipped-cards)))
            (remove-card (:index (last flipped-cards))))
        (-> state
            (flip-card-back (:index (first flipped-cards)))
            (flip-card-back (:index (last flipped-cards)))))))

(defn check-choices [state]
      (let [flipped-count  (->> (get-in state [:game :cards])
                                vals
                                (filter (fn [card]
                                            (= (card :status) :front)))
                                count)]
           (if (= flipped-count 2)
             (-> state
                 (flip-back-or-remove-cards))
             state)))

(register-handler
  :memory/check-choices
  (fn [state _]
      (check-choices state)))

(register-handler
  :memory/flip-card
  (fn [state [_ card]]
      (-> state
          (check-choices)
          (flip-card-front (card :index)))))

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
