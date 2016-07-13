 (ns vivavocab.views
   (:require [re-frame.core :refer [dispatch subscribe]]
             [vivavocab.styles :as styles]
             [vivavocab.games.flash.views :refer [game-view]]
             [vivavocab.games.flash.styles :as flash]
             [vivavocab.games.memory.views :as memory]
             ))

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
                                [level-view level-id])]))])))

(defn app-view []
      (let [level (subscribe [:view])]
           (fn []
               [memory/game-view]
               #_[:div.app
                [styles/styles-view]
                [flash/styles-view]
                (case @level
                      :levels [levels-view]
                      :game-end [win-view {:retry :retry
                                           :back-to-levels :back-to-levels
                                           :next-level :next-level}]
                      :game [game-view])])))
