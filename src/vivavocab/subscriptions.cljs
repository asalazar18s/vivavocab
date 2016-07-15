(ns vivavocab.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))


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
