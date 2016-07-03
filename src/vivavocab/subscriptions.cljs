(ns vivavocab.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
  :question
  (fn [state _]
      (reaction (get-in @state [:level :question]))))

(register-sub
  :prompt-word
  (fn [state _]
      (let [prompt-id (reaction (get-in @state [:level :question :prompt :id]))
            prompt-key (reaction (get-in @state [:level :question :prompt-key]))]
           (reaction (get-in @state [:words @prompt-id @prompt-key])))))

(register-sub
  :choice-word
  (fn [state [_ id]]
      (let [choice-key (reaction (get-in @state [:level :question :choice-key]))]
           (reaction (get-in @state [:words id @choice-key])))))

(register-sub
  :progress
  (fn [state _]
      (reaction (get-in @state [:level :progress]))))

(register-sub
  :character-mood
  (fn [state _]
      (reaction (get-in @state [:level :character-mood]))))

(register-sub
  :character-sprite
  (fn [state _]
      (let [level-id (reaction (get-in @state [:level :id]))
            episode-id (reaction (get-in @state [:levels @level-id :episode-id]))]
           (reaction (get-in @state [:episodes @episode-id :character-sprite])))))

(register-sub
  :levels
  (fn [state _]
      (reaction (vals (get-in @state [:levels])))))

(register-sub
  :level
  (fn [state _]
      (reaction (get-in @state [:level]))))
