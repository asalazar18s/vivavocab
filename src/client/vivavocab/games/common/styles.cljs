(ns vivavocab.games.common.styles
  (:require  [garden.core :as garden]
             [re-frame.core :refer [subscribe]]))

(defn styles-view []
      (fn []
          [:style
           (garden/css
             [:.back-button
              {:position "absolute"
               :width "50px"
               :height "50px"
               :background "red"}]

             [:.win-view
              {:height           "100%"
               :width            "100%"
               :background-color "red"}

              [:.next
               {:width            "50px"
                :height           "50px"
                :background-color "cyan"
                :position         "absolute"}]

              [:.retry
               {:width            "50px"
                :height           "50px"
                :background-color "blue"}]

              [:.menu
               {:width            "50px"
                :height           "50px"
                :background-color "green"}]])]))
