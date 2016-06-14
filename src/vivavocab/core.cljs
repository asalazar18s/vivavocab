(ns vivavocab.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as r]
            [garden.core :as garden]
            [clojure.string :as string]
            [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch
                                   dispatch-sync
                                   subscribe
                                   after]]
            [schema.core :as s]))

(enable-console-print!)

(def initial-state
  {:words [{:text "apple" :translation "manzana"}
           {:text "orange" :translation "naranja"}
           {:text "pear" :translation "pera"}
           {:text "banana" :translation "banana"}]})

(def schema
  {:words [{:text s/Str :translation s/Str}]})

(defn valid-schema?
  "validate the given state, writing any problems to console.error"
  [state]
  (let [res (s/check schema state)]
    (if (some? res)
      (.error js/console (str "schema problem: " res)))))

(def middleware
  [(after valid-schema?)])

(register-handler
  :initialize
  middleware
  (fn [state _]
    (merge state initial-state)))

(register-sub
  :words
  (fn [state _]
    (reaction (@state :words))))

(register-sub
  :random-word
  (fn [state _]
    (reaction (rand-nth (@state :words)))))

(def styles
  (garden/css
    [:.app-view
     [:.card
      {:width "100px"
       :height "100px"
       :border "1px solid black"}]]))

(defn words-view []
  (let [words (subscribe [:words])]
    (fn []
      [:div.words-view
       (for [word @words]
         [:div.word.card
          (word :text)])])))

(defn prompt-view []
  (let [random-word (subscribe [:random-word])]
    (fn []
      [:div.prompt.card
       (:translation @random-word)])))

(defn app-view []
  [:div.app-view
   [:style styles]
   [prompt-view]
   [words-view]])

(defn ^:export run
  []
  (dispatch-sync [:initialize])
  (r/render
    [app-view]
    (js/document.getElementById "app")))
