(ns vivavocab.handlers
  (:require [re-frame.core :refer [register-handler]]
            [vivavocab.helpers :refer [get-episode-id]]
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

   :view :levels ; :game :game-end

   :level {:level-id 3
           :stars 0}})

(register-handler
  :initialize
  (fn [state _]
      (merge state initial-state)))

(defn set-level [state level-id]
  (-> state
      (assoc :view :game)
      (assoc :level {:id level-id
                     :progress 0
                     :character-mood :neutral})
      (set-new-words)))

(register-handler
  :choose-level
  (fn [state [_ level-id]]
      (set-level state level-id)))

(defn back-to-levels [state]
      (-> state
          (assoc :level nil)
          (assoc :view :levels)))

(register-handler
  :back-to-levels
  (fn [state _]
      (back-to-levels state)))

(register-handler
  :retry
  (fn [state _]
      (let [level-id (get-in state [:level :id])]
           (set-level state level-id))))

(register-handler
  :next-level
  (fn [state _]
      (let [level-id (get-in state [:level :id])
            episode-id (get-episode-id state level-id)
            level-ids (get-in state [:episodes episode-id :level-ids])
            next-level-id (->> level-ids
                               (drop-while (fn [id]
                                               (not= id level-id)))
                               second)]
      (if next-level-id
        (set-level state next-level-id)
        (back-to-levels state)))))
