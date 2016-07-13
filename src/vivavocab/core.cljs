(ns vivavocab.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch-sync]]
            [vivavocab.views :as views]
            [vivavocab.handlers]
            [vivavocab.subscriptions]
            [vivavocab.games.flash.core]
            [vivavocab.games.memory.core]))

(enable-console-print!)

(defn ^:export run
      []
      (dispatch-sync [:initialize])
      (r/render
        [views/app-view]
        (js/document.getElementById "app")))

(defn ^:export reload
      []
      (run))
