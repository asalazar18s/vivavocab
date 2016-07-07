(ns vivavocab.games.memory.styles
  (:require  [garden.core :as garden]
             [re-frame.core :refer [subscribe]]))

(def card-size 140)
(def character-sprite "farmer")

(defn character-styles []

               [:.character
                {:background-image (str "url(/episodes/" (str character-sprite) "/char_neutral.png)")
                 :background-size "contain"
                 :width (str (* 664 0.40) "px")
                 :height (str (* 1133 0.40) "px")
                 :position "absolute"
                 :box-sizing "border-box"
                 :top "15%"}

                [:&.angry
                 {:background-image (str "url(/episodes/" (str character-sprite) "/char_angry.png)")}]

                [:&.happy
                 {:background-image (str "url(/episodes/" (str character-sprite) "/char_happy.png)")}]]
               )

(defn styles-view []
      [:style
       (garden/css
         [:.game.memory
          {:width "100vw"
           :height "100vh"}

          (character-styles)

          [:.cards
           {:display "flex"
            :flex-wrap "wrap-reverse"
            :flex-direction "column"
            :justify-content "space-between"
            :height "100vh"
            :width "75vw"
            :position "absolute"
            :right 0
            :padding "3vw"
            :box-sizing "border-box"}

           [:.card
            {

             :width (str card-size "px")
             :height (str card-size "px")
             :font-size "25px"
             :font-family "Helvetica"
             :line-height (str card-size "px")
             :background-size "contain"
             :text-align "center"}
            [:&.back
             {:background-image "url(/images/game/choice_bg_wrong.png)"}]
            [:&.gone
             {:background-image ""}]
            [:&.flipped
             {:background-image "url(/images/game/choice_bg.png)"}]

            ]
           ]

          ]
         )
       ]


      )
