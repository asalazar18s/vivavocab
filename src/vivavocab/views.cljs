 (ns vivavocab.views
   (:require [re-frame.core :refer [dispatch subscribe]]
             [vivavocab.styles :as styles]
             [vivavocab.games.flash.views :refer [game-view]]
             [vivavocab.games.flash.styles :as flash]
             [vivavocab.games.memory.views :as memory]
             [vivavocab.games.common.views :refer [win-view]]))

(defn level-view [level-id]
      (let [level (subscribe [:level level-id])]
           [:div.level {:on-click (fn [_]
                                      (dispatch [:choose-level level-id]))}
            (@level :name)]))

(defn levels-view []
      (let [episodes (subscribe [:episodes])]
           (fn []
               [:div.episodes
                (for [episode @episodes]
                     (let [level-ids (episode :level-ids)]
                          [:div.levels
                           (for [level-id level-ids]
                                [level-view level-id])]))
                [:div.memory-game {:on-click (fn [_]
                                                 (dispatch [:go-to-memory-game]))}]])))

(defn app-view []
      (let [level (subscribe [:view])]
           (fn []
               [:div.app
                [styles/styles-view]
                [flash/styles-view]
                (case @level
                      :levels [levels-view]
                      :memory-game [memory/game-view]
                      :game-end [win-view {:retry :flash/retry
                                           :back-to-levels :flash/back-to-levels
                                           :next-level :flash/next-level}]
                      :game [game-view])])))
