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
  {:levels {3 {:id 3 :name "Level 3" :word-ids [1 5 7 9 10] :character-sprite "teacher"}
            4 {:id 4 :name "Level 4" :word-ids [10 12 15 16] :character-sprite "farmer"}}

   :words {1 {:id 1 :text "apple" :translation "manzana" :image "apple-image"}
           5 {:id 5 :text "orange" :translation "naranja" :image "orange-image"}
           7 {:id 7 :text "pear" :translation "pera" :image "pear-image"}
           9 {:id 9 :text "banana" :translation "banana" :image "banana-image"}
           10 {:id 10 :text "grapes" :translation "uvas" :image "grapes-image"}
           12 {:id 12 :text "raspberry" :translation "frambuesa" :image "raspberry-image"}
           15 {:id 15 :text "tangerine" :translation "mandarina" :image "tangerine-image"}
           16 {:id 16 :text "pomegranate" :translation "granada" :image "pomegranate-image"}}

   :key-options #{:text :translation :image}

   :level nil})

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
      (let [prompt-id (get-in state [:level :question :prompt])
            result (= prompt-id choice-id)
            choices (get-in state [:level :question :choices])
            index (first (keep-indexed (fn [i choice]
                                           (when (= (choice :id) choice-id)
                                                 i))
                                       choices))]
           (assoc-in state [:level :question :choices index :correct?] result)))

(defn set-new-words [state]
      (let [word-count 4
            level-id (get-in state [:level :id])
            choice-ids (->> (get-in state [:levels level-id :word-ids])
                            shuffle
                            (take word-count)
                            vec)
            prompt-id (-> choice-ids
                          rand-nth)
            prompt-key (-> state
                           :key-options
                           vec
                           rand-nth)
            choice-key (-> state
                           :key-options
                           (disj prompt-key)
                           vec
                           rand-nth)]

           (assoc-in state
                     [:level :question]
                     {:prompt-key prompt-key
                      :choice-key choice-key
                      :prompt {:id prompt-id}
                      :choices (->> ; (0 1 ...)
                                    (take word-count (iterate inc 0))
                                    (map (fn [index]
                                             {:id (choice-ids index)
                                              :correct nil}))
                                    vec)})))

(defn update-progress [state]
      (update-in state [:level :progress] + 0.1))

(defn update-when-correct [state choice-id]
      (let [prompt-id (get-in state [:level :question :prompt :id])
            correct? (= prompt-id choice-id)]
           (if correct?
             (-> state
                 update-progress
                 set-new-words)
             state)))

(defn update-character-mood [state choice-id]
      (let [prompt-id (get-in state [:level :question :prompt :id])
            correct? (= prompt-id choice-id)]
           (assoc-in state [:level :character-mood] (if correct?
                                                               :happy
                                                               :angry))))

(register-handler
  :guess
  (fn [state [_ choice-id]]
      (-> state
          (update-choice-status choice-id)
          (update-character-mood choice-id)
          (update-when-correct choice-id))))

(register-handler
  :choose-level
  (fn [state [_ level-id]]
      (-> state
          (assoc :level {:id level-id
                         :progress 0
                         :character-mood :neutral})
          (set-new-words))))

; subscribe functions

(register-sub
  :question
  (fn [state _]
      (reaction (get-in @state [:level :question]))))

(register-sub
  :prompt-word
  (fn [state _]
      (let [prompt-id (reaction (get-in @state [:level :question :prompt :id]))
            prompt-key (reaction (get-in @state [:level :question :prompt-key]))]
        (reaction (get-in @state [:words @prompt-id @prompt-key])))))

(register-sub
  :choice-word
  (fn [state [_ id]]
      (let [choice-key (reaction (get-in @state [:level :question :choice-key]))]
           (reaction (get-in @state [:words id @choice-key])))))

(register-sub
  :progress
  (fn [state _]
      (reaction (get-in @state [:level :progress]))))

(register-sub
  :character-mood
  (fn [state _]
      (reaction (get-in @state [:level :character-mood]))))

(register-sub
  :character-sprite
  (fn [state _]
      (let [level-id (reaction (get-in @state [:level :id]))]
           (reaction (get-in @state [:levels @level-id :character-sprite])))))

(register-sub
  :levels
  (fn [state _]
      (reaction (vals (get-in @state [:levels])))))

(register-sub
  :level-id
  (fn [state _]
      (reaction (get-in @state [:level :id]))))

; styles

(defn styles-view []
      (let [character-sprite (subscribe [:character-sprite])]
           (fn []
               [:style
                (garden/css
                  [:body
                   {:margin "0"
                    :padding "0"}]
                  [:.app
                   [:.floor
                    {:position "absolute"
                     :background-image (str "url(/episodes/" (str @character-sprite) "/floor.png)")
                     :height "20vh"
                     :width "100vw"
                     :bottom "0"}]
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
                   [:.background
                    {:background (str "url(/episodes/" (str @character-sprite) "/bg.png)" " repeat-y")
                     :position "absolute"
                     :width "100vw"
                     :height "100vh"
                     :top 0
                     :left 0}]
                   [:.levels
                    {:background-color "cyan"
                     :position "absolute"
                     :width "100vw"
                     :height "100vh"}
                    [:.level
                     {:width "100px"
                      :height "100px"
                      :border "1px solid black"}]]
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

; views

(defn choice-view [choice]
  (let [word (subscribe [:choice-word (choice :id)])]
    (fn [choice]
      [:div.choice.card
       {:class (case (choice :correct?)
                 true "correct"
                 false "incorrect"
                 nil "")
        :on-click (fn []
                    (dispatch [:guess (choice :id)]))}
       @word])))

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
                  @prompt-word]])))

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
                {:class
                 (case @mood
                       :happy "happy"
                       :angry "angry"
                       :neutral "")}])))

(defn floor-view[]
      (fn []
          [:div.floor]))

(defn level-view [level]
      [:div.level {:on-click (fn [_]
                                 (dispatch [:choose-level (level :id)]))}
       (level :name)])

(defn levels-view []
      (let [levels (subscribe [:levels])]
           (fn []
               [:div.levels
                (for [level @levels]
                     [level-view level])])))

(defn game-view[]
      [:div
       [:div.background]
       [character-view]
       [progress-bar-view]
       [prompt-view]
       [floor-view]
       [choices-view]])

(defn app-view []
      (let [level-id (subscribe [:level-id])]
           (fn []
               [:div.app
                [styles-view]
                (if (nil? @level-id)
                  [levels-view]
                  [game-view])])))

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
