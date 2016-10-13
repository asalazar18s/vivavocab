(ns vivavocab.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch-sync]]
            [vivavocab.views :as views]
            [vivavocab.handlers]
            [vivavocab.subscriptions]
            [vivavocab.games.flash.core]
            [vivavocab.games.memory.core]))

(enable-console-print!)

(defn render
      []
      (r/render
        [views/app-view]
        (js/document.getElementById "app")))

(defn ^:export run
      []
      (dispatch-sync [:initialize])
      (render))

(defn ^:export reload
      []
      (render))
