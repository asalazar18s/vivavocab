(ns vivavocab.games.memory.handlers
  (:require [re-frame.core :refer [register-handler]]))

(register-handler
  :memory/initialize
  (fn [state _]
      (assoc-in state [:game :cards] {0 {:status :flipped
                                         :index 0
                                         :value 1}
                                      1 {:status :flipped
                                         :index 1
                                         :value 2}
                                      2 {:status :flipped
                                         :index 2
                                         :value 3}
                                      3 {:status :flipped
                                         :index 3
                                         :value 4}
                                      4 {:status :flipped
                                         :index 4
                                         :value 5}
                                      5 {:status :flipped
                                         :index 5
                                         :value 6}
                                      6 {:status :flipped
                                         :index 6
                                         :value 6}
                                      7 {:status :flipped
                                         :index 7
                                         :value 5}
                                      8 {:status :flipped
                                         :index 8
                                         :value 4}
                                      9 {:status :flipped
                                         :index 9
                                         :value 3}
                                      10 {:status :flipped
                                          :index 10
                                          :value 2}
                                      11 {:status :flipped
                                          :index 11
                                          :value 1}})))

(register-handler
  :memory/flip-card
  (fn [state [_ card]]
      (assoc-in state [:game :cards (card :index) :status] :back)))
