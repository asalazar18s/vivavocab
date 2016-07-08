(ns vivavocab.games.memory.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
  :memory/cards
  (fn [state _]
      (reaction (vals (get-in @state [:game :cards])))))
