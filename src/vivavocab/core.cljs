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

   :progress 0

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

(defn update-choice-status [state choice-id]
      (let [prompt-id (get-in state [:question :prompt])
            result (= prompt-id choice-id)
            choices (get-in state [:question :choices])
            index (first (keep-indexed (fn [i choice]
                                           (when (= (choice :id) choice-id)
                                                 i))
                                       choices))]
           (assoc-in state [:question :choices index :correct?] result)))

(defn set-new-words [state]
      (let [choice-ids (->> state
                            :words
                            keys
                            shuffle
                            (take 4)
                            vec)
            prompt-id (->> choice-ids
                           shuffle
                           first)]
           (-> state
               (assoc-in [:question :prompt] prompt-id)
               (assoc-in [:question :choices 0] {:id (choice-ids 0) :correct? nil})
               (assoc-in [:question :choices 1] {:id (choice-ids 1) :correct? nil})
               (assoc-in [:question :choices 2] {:id (choice-ids 2) :correct? nil})
               (assoc-in [:question :choices 3] {:id (choice-ids 3) :correct? nil}))))

(defn update-progress [state]
      (update-in state [:progress] + 0.1))

(defn update-when-correct [state choice-id]
      (let [prompt-id (get-in state [:question :prompt])
            correct? (= prompt-id choice-id)]
           (if correct?
             (-> state
                 update-progress
                 set-new-words)
             state)))

(register-handler
  :guess
  (fn [state [_ choice-id]]
      (-> state
          (update-choice-status choice-id)
          (update-when-correct choice-id))))

; subscribe functions

(register-sub
  :question
  (fn [state _]
      (reaction (@state :question))))

(register-sub
  :words
  (fn [state _]
      (reaction (@state :words))))

(register-sub
  :progress
  (fn [state _]
      (reaction (@state :progress))))

; styles

(def styles
  (garden/css
    [:.app-view
     [:.progress-bar
      {:width "100%"
       :height "80px"
       :background "grey"}
      [:.progress
       {:height "100%"
        :background "green"
        :transition "width 0.5s ease-in-out"}]]
     [:.prompt
      {:background "white"
       :width "120px"
       :height "120px"
       :border "3px solid black"
       :font-size "22px"
       :font-family "Arial"
       :line-height "120px"
       :margin "auto"
       :text-align "center"
       :text-transform "uppercase"}]
     [:.choice
      {:background "cyan"
       :width "80px"
       :height "80px"
       :border "2px dashed black"
       :font-style "italic"
       :font-size "16px"
       :font-family "Helvetica"
       :line-height "80px"
       :float "left"
       :margin "50px 75px 0"
       :text-align "center"}
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
                     [:div.choice.card
                      {:class (case (word :correct?)
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

(defn progress-bar-view []
      (let [progress (subscribe [:progress])]
           (fn []
               [:div.progress-bar
                [:div.progress {:style {:width (str (* @progress 100) "%")}}]])))


(defn app-view []
      [:div.app-view
       [:style styles]
       [progress-bar-view]
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
