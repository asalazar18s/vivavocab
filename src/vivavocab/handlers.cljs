(ns vivavocab.handlers
  (:require [re-frame.core :refer [register-handler dispatch]]
            [vivavocab.games.flash.handlers :refer [set-new-words]]))

(def initial-state
  {:episodes {123 {:id 123 :character-sprite "teacher" :level-ids [3]}
              456 {:id 456 :character-sprite "farmer" :level-ids [4]}}

   :levels {3 {:id 3 :name "Level 3" :word-ids [1 5 7 9 10]}
            4 {:id 4 :name "Level 4" :word-ids [10 12 15 16]}}

   :words {1 {:id 1 :text "apple" :translation "manzana" :image "apple-image"}
           5 {:id 5 :text "orange" :translation "naranja" :image "orange-image"}
           7 {:id 7 :text "pear" :translation "pera" :image "pear-image"}
           9 {:id 9 :text "banana" :translation "banana" :image "banana-image"}
           10 {:id 10 :text "grapes" :translation "uvas" :image "grapes-image"}
           12 {:id 12 :text "raspberry" :translation "frambuesa" :image "raspberry-image"}
           15 {:id 15 :text "tangerine" :translation "mandarina" :image "tangerine-image"}
           16 {:id 16 :text "pomegranate" :translation "granada" :image "pomegranate-image"}}

   :key-options #{:text :translation :image}

   :view :levels ; :game :game-end :memory-game

   :level {:level-id 3
           :stars 0}})

(register-handler
  :initialize
  (fn [state _]
      (merge state initial-state)))

(register-handler
  :choose-level
  (fn [state [_ level-id]]
      (dispatch [:flash/initialize level-id])
      state))

(register-handler
  :go-to-memory-game
  (fn [state _]
      (dispatch [:memory/initialize])
      state))

(register-handler
  :retry
  (fn [state _]
      (let [level-id (get-in state [:level :id])]
           (dispatch [:flash/initialize level-id])
           state)))

