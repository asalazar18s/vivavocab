(ns vivavocab.games.common.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reanimated.core :as anim]
            [vivavocab.games.common.styles :refer [styles-view]]))

(defn win-view [{:keys [back-to-levels retry next-level]}]
      (fn []
          [:div.win-view
           [styles-view]
           [:div.character]
           [:div.bubble
            [:div.stars
             [:div.star]
             [:div.star]
             [:div.star]]
            [:div.message]]
           [:div.buttons
            [:div.retry {:on-click (fn [_]
                                       (dispatch [retry]))}]
            [:div.menu {:on-click (fn [_]
                                      (dispatch [back-to-levels]))}]
            [:div.next {:on-click (fn [_]
                                      (dispatch [next-level]))}]]]))
