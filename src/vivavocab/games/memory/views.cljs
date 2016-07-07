(ns vivavocab.games.memory.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reanimated.core :as anim]
            [vivavocab.games.memory.styles :refer [styles-view]]))

(def cards [1 2 3 4 5 6 7 8 9 10 11 12])

(defn game-view []
      (fn []
          [:div.game.memory
           [styles-view]
           [:div.character]
           [:div.cards
            (for [card cards]
                 [:div.card {:class "flipped"} card])]]))
