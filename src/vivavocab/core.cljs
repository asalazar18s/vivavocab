(ns vivavocab.core
  (:require [reagent.core :as r]
            [garden.core :as garden]
            [clojure.string :as string]))

(enable-console-print!)

(def styles
  (garden/css
    [:.app-view
       [:.card
        {:width "100px"
         :height "100px"
         :border "1px solid black"}]]))

(def state
  {:words [{:text "apple" :translation "manzana"}
           {:text "orange" :translation "naranja"}
           {:text "pear" :translation "pera"}
           {:text "banana" :translation "banana"}]})

(defn app-view []
      [:div.app-view
         [:style styles]

        (let [random-word (rand-nth (state :words))]
         [:div.prompt.card
          (:translation random-word)])

        (for [word (state :words)]
             [:div.word.card
              (word :text)])])

(r/render
  [app-view]
  (js/document.getElementById "app"))
