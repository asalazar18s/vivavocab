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
            [reanimated.core :as anim]
            [schema.core :as s]))

(enable-console-print!)

(def initial-state
  {:words {1 {:id 1 :text "apple" :translation "manzana"}
           5 {:id 5 :text "orange" :translation "naranja"}
           7 {:id 7 :text "pear" :translation "pera"}
           9 {:id 9 :text "banana" :translation "banana"}
           10 {:id 10 :text "papaya" :translation "papaya"}}

   :progress 0

   :character-mood :neutral

   :question {:prompt {:id 1}
              :choices [{:id 1 :correct? nil}
                        {:id 5 :correct? nil}
                        {:id 7 :correct? nil}
                        {:id 9 :correct? nil}]}})

(def schema
  {:words {s/Num {:id s/Num
                  :text s/Str
                  :translation s/Str}}
   :progress s/Num
   :character-mood (s/enum :neutral :happy :angry)
   :question {:prompt {:id s/Num}
              :choices [{:id s/Num
                         :correct? (s/maybe s/Bool)}]}})

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
               (assoc-in [:question :prompt :id] prompt-id)
               (assoc-in [:question :choices 0] {:id (choice-ids 0) :correct? nil})
               (assoc-in [:question :choices 1] {:id (choice-ids 1) :correct? nil})
               (assoc-in [:question :choices 2] {:id (choice-ids 2) :correct? nil})
               (assoc-in [:question :choices 3] {:id (choice-ids 3) :correct? nil}))))

(defn update-progress [state]
      (update-in state [:progress] + 0.1))

(defn update-when-correct [state choice-id]
      (let [prompt-id (get-in state [:question :prompt :id])
            correct? (= prompt-id choice-id)]
           (if correct?
             (-> state
                 update-progress
                 set-new-words)
             state)))

(defn update-character-mood [state choice-id]
      (let [prompt-id (get-in state [:question :prompt :id])
            correct? (= prompt-id choice-id)]
           (assoc-in state [:character-mood] (if correct?
                                               :happy
                                               :angry))))

(register-handler
  :guess
  (fn [state [_ choice-id]]
      (-> state
          (update-choice-status choice-id)
          (update-character-mood choice-id)
          (update-when-correct choice-id))))

; subscribe functions

(register-sub
  :question
  (fn [state _]
      (reaction (@state :question))))

(register-sub
  :prompt-word
  (fn [state _]
      (let [prompt (reaction (get-in @state [:question :prompt]))]
        (reaction (get-in @state [:words (@prompt :id)])))))

(register-sub
  :word
  (fn [state [_ id]]
      (reaction (get-in @state [:words id]))))

(register-sub
  :progress
  (fn [state _]
      (reaction (@state :progress))))

(register-sub
  :character-mood
  (fn [state _]
      (reaction (@state :character-mood))))

; styles

(def styles
  (garden/css
    [:body
     {:margin "0"
      :padding "0"}]
    [:.app
     [:.floor
      {:background-image "url(/episodes/farmer/floor.png)"
       :position "absolute"
       :height "20vh"
       :width "100vw"
       :bottom "0"}]
     [:.character
      {:background-image "url(/episodes/farmer/char_neutral.png)"
       :background-size "contain"
       :width (str (* 664 0.40) "px")
       :height (str (* 1133 0.40) "px")
       :position "absolute"
       :box-sizing "border-box"
       :top "15%"}
      [:&.angry
       {:background-image "url(/episodes/farmer/char_angry.png)"}]
      [:&.happy
       {:background-image "url(/episodes/farmer/char_happy.png)"}]]
     [:.background
      {:background "url(/episodes/farmer/bg.png) repeat-y"
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
      {:background-image "url(/episodes/farmer/prompt_bg.png)"
       :background-size "contain"
       :width (str (* 480 0.5) "px")
       :height (str (* 671 0.5) "px")
       :position "absolute"
       :padding-top "140px"
       :box-sizing "border-box"
       :top "9%"
       :left "50%"}
      [:.prompt
       {:font-size "22px"
        :font-family "Arial"
        :line-height "120px"
        :width "120px"
        :height "120px"
        :margin "auto"
        :text-align "center"}]]
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
             {:background-image "url(/images/game/choice_bg_wrong.png)"}]])]]))

; views

(defn choice-view [choice]
  (let [word (subscribe [:word (choice :id)])]
    (fn [choice]
      [:div.choice.card
       {:class (case (choice :correct?)
                 true "correct"
                 false "incorrect"
                 nil "")
        :on-click (fn []
                    (dispatch [:guess (choice :id)]))}
       (@word :text)])))

(defn choices-view []
      (let [question (subscribe [:question])]
           (fn []
               [:div.choices
                (doall
                  (for [choice (@question :choices)]
                    ^{:key (choice :id)}
                    [choice-view choice]))])))

(defn prompt-view []
      (let [prompt-word (subscribe [:prompt-word])]
           (fn []
               [:div.prompt-background
                [:div.prompt
                  (:translation @prompt-word)]])))

(defn progress-bar-view []
      (let [progress (subscribe [:progress])
            progress-anim (anim/interpolate-to progress {:duration 500})]
           (fn []
             [:div.progress-bar
              [:div.progress {:style {:width (str (* @progress-anim 100) "%")}}]])))

(defn character-view []
      (let [mood (subscribe [:character-mood])]
           (fn []
               [:div.character
                {:class  (case @mood
                               :happy "happy"
                               :angry "angry"
                               :neutral "")}])))
(defn floor-view[]
      (fn []
          [:div.floor]))

(defn app-view []
      [:div.app
       [:style styles]
       [:div.background]
       [character-view]
       [progress-bar-view]
       [prompt-view]
       [floor-view]
       [choices-view]])

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
