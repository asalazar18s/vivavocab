(ns vivavocab.games.flash.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reanimated.core :as anim]
            [vivavocab.games.flash.styles :refer [styles-view]]))

(defn choice-view [choice]
      (let [word (subscribe [:choice-word (choice :id)])]
           (fn [choice]
               [:div.choice.card
                {:class (case (choice :correct?)
                              true "correct"
                              false "incorrect"
                              nil "")
                 :on-click (fn []
                               (dispatch [:guess (choice :id)]))}
                @word])))

(defn choices-view []
      (let [question (subscribe [:question])]
           (fn []
               [:div.choices
                (doall
                  (for [choice (@question :choices)]
                       ^{:key (choice :id)}
                       [choice-view choice]))])))

(defn prompt-view []
      (let [prompt-word (subscribe [:prompt-word])]
           (fn []
               [:div.prompt-background
                [:div.prompt
                 @prompt-word]])))

(defn progress-bar-view []
      (let [progress (subscribe [:progress])
            progress-anim (anim/interpolate-to progress {:duration 500})]
           (fn []
               [:div.progress-bar
                [:div.progress
                 {:style {:width (str (* @progress-anim 100) "%")}}]])))

(defn character-view []
      (let [mood (subscribe [:character-mood])]
           (fn []
               [:div.character
                {:class
                 (case @mood
                       :happy "happy"
                       :angry "angry"
                       :neutral "")}])))

(defn floor-view[]
      (fn []
          [:div.floor]))

(defn back-button-view []
      (fn []
          [:div.back-button
           {:on-click (fn [_]
                          (dispatch [:back-to-levels]))}]))

(defn win-view []
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
                                       (dispatch [:retry]))}]
            [:div.menu {:on-click (fn [_]
                                      (dispatch [:back-to-levels]))}]
            [:div.next {:on-click (fn [_]
                                      (dispatch [:next-level]))}]]]))

(defn game-view []
      (fn []
          [:div.game.flash
           [:div.background]
           [character-view]
           [progress-bar-view]
           [back-button-view]
           [prompt-view]
           [floor-view]
           [choices-view]]))
