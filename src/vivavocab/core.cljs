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
  {:words {1 {:id 1 :text "apple" :translation "manzana"}
           5 {:id 5 :text "orange" :translation "naranja"}
           7 {:id 7 :text "pear" :translation "pera"}
           9 {:id 9 :text "banana" :translation "banana"}
           10 {:id 10 :text "papaya" :translation "papaya"}}

   :question {:prompt 1
              :choices [{:id 1 :correct? nil}
                        {:id 5 :correct? nil}
                        {:id 7 :correct? nil}
                        {:id 9 :correct? nil}]}})

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

; dispatch functions

(register-handler
  :initialize
  middleware
  (fn [state _]
      (merge state initial-state)))

(register-handler
  :guess
  (fn [state [_ choice-id]]
      (let [prompt-id (get-in state [:question :prompt])
            result (= prompt-id choice-id)
            choices (get-in state [:question :choices])
            index (first (keep-indexed (fn [i choice]
                                           (when (= (choice :id) choice-id)
                                                 i))
                                       choices))]
           (assoc-in state [:question :choices index :correct?] result))))

; subscribe functions

(register-sub
  :question
  (fn [state _]
      (reaction (@state :question))))

(register-sub
  :words
  (fn [state _]
      (reaction (@state :words))))

; styles

(def styles
  (garden/css
    [:.app-view
     [:.card
      {:width  "100px"
       :height "100px"
       :border "1px solid black"}
      [:&.incorrect
       {:background "red"}]
      [:&.correct
       {:background "green"}]]]))

; views

(defn words-view []
      (let [question (subscribe [:question])
            words (subscribe [:words])]
           (fn []
               [:div.words-view
                (for [word (@question :choices)]
                     [:div.word.card {:class (case (word :correct?)
                                                   true "correct"
                                                   false "incorrect"
                                                   nil "")
                                      :on-click (fn []
                                                    (dispatch [:guess (word :id)]))}
                      (:text (@words (word :id)))])])))

(defn prompt-view []
      (let [question (subscribe [:question])
            words (subscribe [:words])]
           (fn []
               [:div.prompt.card
                (:translation (@words (@question :prompt)))])))

(defn app-view []
      [:div.app-view
       [:style styles]
       [prompt-view]
       [words-view]])

; run functions

(defn ^:export run
      []
      (dispatch-sync [:initialize])
      (r/render
        [app-view]
        (js/document.getElementById "app")))

(defn ^:export reload
      []
      (run))
