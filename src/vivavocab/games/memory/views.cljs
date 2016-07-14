(ns vivavocab.games.memory.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reanimated.core :as anim]
            [vivavocab.games.memory.styles :refer [styles-view]]
            [vivavocab.games.common.views :refer [win-view]]))

(def timeout (atom nil))

(defn card-view [card]
      (let [card-value (subscribe [:memory/card-value (card :word-id) (card :word-key)])]
           (fn [card]
               [:div.card {:class (name (card :status))
                           :on-click (fn [_] (when (= (card :status) :back)
                                                   (dispatch [:memory/flip-card card])
                                                   (js/clearTimeout @timeout)
                                                   (reset! timeout (js/setTimeout
                                                                     (fn [] (dispatch [:memory/check-choices]))
                                                                     1500))))}
                (when (= (card :status) :front)
                      @card-value)])))

(defn cards-view []
      (let [cards (subscribe [:memory/cards])]
           (fn []
               [:div.cards
                (for [card @cards]
                     [card-view card])])))

(defn level-view []
      [:div
       [styles-view]
       [:div.character]
       [cards-view]])

(defn game-view []
      (let [game-over? (subscribe [:memory/game-over?])]
        (fn []
          [:div.game.memory
           (if @game-over?
             [win-view {:retry :memory/retry
                        :back-to-levels :memory/back-to-levels
                        :next-level :memory/next-level}]
             [level-view])])))

(dispatch [:memory/initialize])
