(ns vivavocab.games.flash.styles
  (:require  [garden.core :as garden]
             [re-frame.core :refer [subscribe]]))

(defn styles-view []
      (let [character-sprite (subscribe [:character-sprite])]
           (fn []
               [:style
                (garden/css
                  [:.win-view
                   {:height "100%"
                    :width "100%"
                    :background-color "red"}

                   [:.next
                    {:width "50px"
                     :height "50px"
                     :background-color "cyan"
                     :position "absolute"}]

                   [:.retry
                    {:width "50px"
                     :height "50px"
                     :background-color "blue"}]

                   [:.menu
                    {:width "50px"
                     :height "50px"
                     :background-color "green"}]]

                  [:.character
                   {:background-image (str "url(/episodes/" (str @character-sprite) "/char_neutral.png)")
                    :background-size "contain"
                    :width (str (* 664 0.40) "px")
                    :height (str (* 1133 0.40) "px")
                    :position "absolute"
                    :box-sizing "border-box"
                    :top "15%"}

                   [:&.angry
                    {:background-image (str "url(/episodes/" (str @character-sprite) "/char_angry.png)")}]

                   [:&.happy
                    {:background-image (str "url(/episodes/" (str @character-sprite) "/char_happy.png)")}]]

                  [:.game.flash

                   [:.back-button
                    {:position "absolute"
                     :width "50px"
                     :height "50px"
                     :background "red"}]

                   [:.floor
                    {:position "absolute"
                     :background-image (str "url(/episodes/" (str @character-sprite) "/floor.png)")
                     :height "20vh"
                     :width "100vw"
                     :bottom "0"}]

                   [:.background
                    {:background (str "url(/episodes/" (str @character-sprite) "/bg.png)" " repeat-y")
                     :position "absolute"
                     :width "100vw"
                     :height "100vh"
                     :top 0
                     :left 0}]

                   [:.progress-bar
                    {:width "100%"
                     :height "80px"
                     :background "grey"
                     :position "absolute"
                     :top 0
                     :left 0}
                    [:.progress
                     {:height "100%"
                      :background "green"}]]

                   [:.prompt-background
                    {:background-image (str "url(/episodes/" (str @character-sprite) "/prompt_bg.png)")
                     :background-size "contain"
                     :width (str (* 480 0.5) "px")
                     :height (str (* 671 0.5) "px")
                     :position "absolute"
                     :padding-top "140px"
                     :box-sizing "border-box"
                     :top "9%"
                     :left "50%"}

                    (let [size 120]
                         [:.prompt
                          {:font-size "22px"
                           :font-family "Arial"
                           :line-height (str size"px")
                           :width (str size"px")
                           :height (str size"px")
                           :margin "auto"
                           :text-align "center"}])]

                   [:.choices
                    {:position "absolute"
                     :bottom "2.5vw"
                     :left 0
                     :width "100vw"
                     :display "flex"
                     :justify-content "space-around"}

                    (let [size 140]
                         [:.choice
                          {:background-image "url(/images/game/choice_bg.png)"
                           :width (str size "px")
                           :height (str size "px")
                           :font-size "25px"
                           :font-family "Helvetica"
                           :line-height (str size "px")
                           :background-size "contain"
                           :text-align "center"}
                          [:&.incorrect
                           {:background-image "url(/images/game/choice_bg_wrong.png)"}]])]])])))
