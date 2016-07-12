(ns vivavocab.games.memory.handlers
  (:require [re-frame.core :refer [register-handler]]))

:words {1 {:id 1 :text "apple" :translation "manzana" :image "apple-image"}
        5 {:id 5 :text "orange" :translation "naranja" :image "orange-image"}
        7 {:id 7 :text "pear" :translation "pera" :image "pear-image"}
        9 {:id 9 :text "banana" :translation "banana" :image "banana-image"}
        10 {:id 10 :text "grapes" :translation "uvas" :image "grapes-image"}
        12 {:id 12 :text "raspberry" :translation "frambuesa" :image "raspberry-image"}
        15 {:id 15 :text "tangerine" :translation "mandarina" :image "tangerine-image"}
        16 {:id 16 :text "pomegranate" :translation "granada" :image "pomegranate-image"}}

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
      (assoc-in state [:game :cards] (generate-list state))))

(defn flip-card-up [state card-index]
      (assoc-in state [:game :cards card-index :status] :back))

(register-handler
  :memory/flip-card
  (fn [state [_ card]]
      (-> state
          (flip-card-up (card :index)))))
