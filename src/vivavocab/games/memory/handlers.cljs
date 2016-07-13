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
                                               {:status :flipped
                                                :index index
                                                :value (get-in state [:words (card :word-id) (card :word-key)])})))
                       (reduce (fn [memo card]
                                   (assoc memo (card :index) card)) {})
                       ; {1 {:status flipped
                       ;     :index 1 ...} ...}
                       )]
           cards))

(register-handler
  :memory/initialize
  (fn [state _]
      (assoc-in state [:game] {:cards (generate-list state)
                               :flipped-count 0})))

(defn flip-card-up [state card-index]
      (-> state
          (assoc-in [:game :cards card-index :status] :back)
          (update-in [:game :flipped-count] inc)))

(defn flip-card-back [state card-index]
      (-> state
          (assoc-in [:game :cards card-index :status] :flipped)))

(defn remove-card [state card-index]
      (-> state
          (assoc-in [:game :cards card-index :status] :gone)))

(defn reset-flipped-count [state]
      (assoc-in state [:game :flipped-count] 0))

(defn flip-back-or-remove-cards [state]
      (let [flipped-cards (->> (get-in state [:game :cards])
                               vals
                               (filter (fn [card]
                                           (= (card :status) :back))))]
           (tee flipped-cards)
      (if (= (:word-id (first flipped-cards))
             (:word-id (last flipped-cards)))
        (-> state
            (remove-card (:index (first flipped-cards)))
            (remove-card (:index (last flipped-cards))))
        (-> state
            (flip-card-back (:index (first flipped-cards)))
            (flip-card-back (:index (last flipped-cards)))))))

(defn check-choices [state]
      (let [flipped-count (get-in state [:game :flipped-count])]
           (if (= flipped-count 2)
             (-> state
                 (reset-flipped-count)
                 (flip-back-or-remove-cards))
             state)))

(register-handler
  :memory/flip-card
  (fn [state [_ card]]
      (-> state
          (flip-card-up (card :index))
          (check-choices)
          tee

          ; when 2 flipped: reset flipped count, remove cards if match, etc.
          )))
