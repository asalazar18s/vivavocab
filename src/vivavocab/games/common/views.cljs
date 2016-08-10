(ns vivavocab.games.common.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reanimated.core :as anim]))

(defn win-view [{:keys [back-to-levels retry next-level]}]
      (fn []
          [:div.win-view
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

(defn back-button-view [dispatcher-name]
          [:div.back-button
           {:on-click (fn [_]
                          (dispatch [dispatcher-name]))}])
