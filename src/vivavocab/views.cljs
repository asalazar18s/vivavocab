 (ns vivavocab.views
   (:require [re-frame.core :refer [dispatch subscribe]]
             [vivavocab.styles :as styles]
             [vivavocab.games.common.styles :as common-styles]
             [vivavocab.games.flash.views :as flash]
             [vivavocab.games.memory.views :as memory]))

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
      (let [view (subscribe [:view])]
           (fn []
               [:div.app
                [styles/styles-view]
                [common-styles/styles-view]
                (case @view
                      :levels [levels-view]
                      :memory-game [memory/game-view]
                      :flash-game [flash/game-view])])))
