(ns vivavocab.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]
            [vivavocab.helpers :refer [get-episode-id]]))

(register-sub
  :character-mood
  (fn [state _]
      (reaction (get-in @state [:level :character-mood]))))

(register-sub
  :character-sprite
  (fn [state _]
      (let [level-id (reaction (get-in @state [:level :id]))
            episode-id (reaction (get-episode-id @state @level-id))]
           (reaction (get-in @state [:episodes @episode-id :character-sprite])))))

(register-sub
  :level
  (fn [state [_ level-id]]
      (reaction (get-in @state [:levels level-id]))))

(register-sub
  :view
  (fn [state _]
      (reaction (get-in @state [:view]))))

(register-sub
  :episodes
  (fn [state _]
      (reaction (vals (get-in @state [:episodes])))))

