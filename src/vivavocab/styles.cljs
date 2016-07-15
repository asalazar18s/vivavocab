(ns vivavocab.styles
  (:require [garden.core :as garden]
            [re-frame.core :refer [subscribe]]))

(defn styles-view []
      (fn []
          [:style
           (garden/css
             [:body
              {:margin "0"
               :padding "0"}]

             [:.app

              [:.episodes
               {:width "100vw"
                :height "100vh"
                :background-color "green"}

               [:.levels
                {:display "flex"
                 :padding "50px"}

                [:.level
                 {:width "100px"
                  :height "100px"
                  :border "1px solid black"
                  :margin "10px"}]]]

              [:.memory-game
               {:width "100px"
                :height "100px"
                :background-color "black"}]])]))
