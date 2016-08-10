(ns vivavocab.games.flash.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reanimated.core :as anim]
            [vivavocab.games.flash.styles :refer [styles-view]]
            [vivavocab.games.common.views :refer [win-view]]))

(def timeout (atom nil))

(defn choice-view [choice]
      (let [word (subscribe [:flash/choice-word (choice :id)])]
           (fn [choice]
               [:div.choice.card
                {:class (case (choice :correct?)
                              true "correct"
                              false "incorrect"
                              nil "")
                 :on-click (fn []
                               (dispatch [:flash/guess (choice :id)])
                               (js/clearTimeout @timeout)
                               (reset! timeout (js/setTimeout
                                                 (fn [] (dispatch [:flash/reset-character-mood]))
                                                 1500)))}
                @word])))

(defn choices-view []
      (let [question (subscribe [:flash/question])]
           (fn []
               [:div.choices
                (doall
                  (for [choice (@question :choices)]
                       ^{:key (choice :id)}
                       [choice-view choice]))])))

(defn prompt-view []
      (let [prompt-word (subscribe [:flash/prompt-word])]
           (fn []
               [:div.prompt-background
                [:div.prompt
                 @prompt-word]])))

(defn progress-bar-view []
      (let [progress (subscribe [:flash/progress])
            progress-anim (anim/interpolate-to progress {:duration 500})]
           (fn []
               [:div.progress-bar
                [:div.progress
                 {:style {:width (str (* @progress-anim 100) "%")}}]])))

(defn character-view []
      (let [mood (subscribe [:flash/character-mood])]
           (fn []
               [:div.character
                {:class
                 (case @mood
                       :happy "happy"
                       :angry "angry"
                       :waiting "waiting"
                       :neutral "")}])))

(defn floor-view[]
      (fn []
          [:div.floor]))

(defn back-button-view []
      (fn []
          [:div.back-button
           {:on-click (fn [_]
                          (dispatch [:flash/back-to-levels]))}]))

(defn level-view []
      (fn []
          [:div
           [:div.background]
           [character-view]
           [progress-bar-view]
           [back-button-view]
           [prompt-view]
           [floor-view]
           [choices-view]]))

(defn game-view []
      (let [game-over (subscribe [:flash/game-over?])]
      (fn []
          [:div.game.flash
           [styles-view]
           (if @game-over
             [win-view {:retry :flash/retry
                        :back-to-levels :flash/back-to-levels
                        :next-level :flash/next-level}]
             [level-view])])))
