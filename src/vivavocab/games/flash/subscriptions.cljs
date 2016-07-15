(ns vivavocab.games.flash.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]
            [vivavocab.helpers :refer [get-episode-id]]))

(register-sub
  :flash/character-sprite
  (fn [state _]
      (let [level-id (reaction (get-in @state [:level :id]))
            episode-id (reaction (get-episode-id @state @level-id))]
           (reaction (get-in @state [:episodes @episode-id :character-sprite])))))

(register-sub
  :flash/progress
  (fn [state _]
      (reaction (get-in @state [:level :progress]))))

(register-sub
  :flash/prompt-word
  (fn [state _]
      (let [prompt-id (reaction (get-in @state [:level :question :prompt :id]))
            prompt-key (reaction (get-in @state [:level :question :prompt-key]))]
           (reaction (get-in @state [:words @prompt-id @prompt-key])))))

(register-sub
  :flash/question
  (fn [state _]
      (reaction (get-in @state [:level :question]))))


(register-sub
  :flash/choice-word
  (fn [state [_ id]]
      (let [choice-key (reaction (get-in @state [:level :question :choice-key]))]
           (reaction (get-in @state [:words id @choice-key])))))

(register-sub
  :flash/character-mood
  (fn [state _]
      (reaction (get-in @state [:level :character-mood]))))
