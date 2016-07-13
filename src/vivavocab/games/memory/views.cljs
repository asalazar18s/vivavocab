(ns vivavocab.games.memory.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reanimated.core :as anim]
            [vivavocab.games.memory.styles :refer [styles-view]]))

(def timeout (atom nil))

(defn card-view [card]
      [:div.card {:class (name (card :status))
                  :on-click (fn [_] (when (= (card :status) :flipped)
                                          (dispatch [:memory/flip-card card])
                                          (js/clearTimeout @timeout)
                                          (reset! timeout (js/setTimeout
                                                            (fn [] (dispatch [:memory/check-choices]))
                                                            1500))))}
       (when (= (card :status) :back)
             (card :value))])

(defn cards-view []
      (let [cards (subscribe [:memory/cards])]
           (fn []
               [:div.cards
                (for [card @cards]
                     [card-view card])])))

(defn game-view []
      (fn []
          (dispatch [:memory/initialize])
          [:div.game.memory
           [styles-view]
           [:div.character]
           [cards-view]]))
