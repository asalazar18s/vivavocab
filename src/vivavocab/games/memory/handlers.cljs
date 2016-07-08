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
      (let [word-ids (->> state
                          :words
                          keys
                          shuffle
                          (take 6)
                          cycle
                          (take 12)
                          shuffle)
            key-options (get-in state [:key-options])
            cards (->> word-ids
                       (map-indexed (fn [index word-id]
                                        {:status :flipped
                                         :index index
                                         :word-id word-id
                                         :word-key :text
                                         :value word-id}))
                       (reduce (fn [memo card]
                                   (assoc memo (card :index) card)) {}))]
           cards))

(register-handler
  :memory/initialize
  (fn [state _]
      (assoc-in state [:game :cards] (generate-list state))))

(register-handler
  :memory/flip-card
  (fn [state [_ card]]
      (assoc-in state [:game :cards (card :index) :status] :back)))
