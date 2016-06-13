(ns vivavocab.core
  (:require [reagent.core :as r]
            [garden.core :as garden]
            [clojure.string :as string]))

(enable-console-print!)

(println "Hello Console!")

(defn app-view [] [:div "Hello World!"])

(r/render
  [app-view]
  (js/document.getElementById "app"))