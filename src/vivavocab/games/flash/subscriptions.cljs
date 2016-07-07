(ns vivavocab.games.flash.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
  :progress
  (fn [state _]
      (reaction (get-in @state [:level :progress]))))

(register-sub
  :prompt-word
  (fn [state _]
      (let [prompt-id (reaction (get-in @state [:level :question :prompt :id]))
            prompt-key (reaction (get-in @state [:level :question :prompt-key]))]
           (reaction (get-in @state [:words @prompt-id @prompt-key])))))

(register-sub
  :question
  (fn [state _]
      (reaction (get-in @state [:level :question]))))


(register-sub
  :choice-word
  (fn [state [_ id]]
      (let [choice-key (reaction (get-in @state [:level :question :choice-key]))]
           (reaction (get-in @state [:words id @choice-key])))))
