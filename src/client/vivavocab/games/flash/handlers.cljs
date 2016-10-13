(ns vivavocab.games.flash.handlers
  (:require [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
            [vivavocab.helpers :refer [get-episode-id]]))

(defn set-new-words [state]
      (let [word-count 4
            level-id (get-in state [:level :id])
            previous-prompt-id (get-in state [:level :question :prompt :id])
            prompt-id (-> (get-in state [:levels level-id :word-ids])
                          set
                          (disj previous-prompt-id)
                          vec
                          rand-nth)
            choice-ids (-> (get-in state [:levels level-id :word-ids])
                           set
                           (disj prompt-id)
                           shuffle
                           (->> (take (dec word-count)))
                           vec
                           (conj prompt-id)
                           shuffle)
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

(def no-actions-timeout (atom nil))

(defn set-no-actions-timeout! []
      (js/clearTimeout @no-actions-timeout)
      (reset! no-actions-timeout
              (js/setTimeout
                (fn [] (dispatch [:flash/no-actions]))
                5000)))

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

(defn maybe-set-win-state [state]
      (let [progress (get-in state [:level :progress])
            level-id (get-in state [:level :id])]
           (if (> progress 1.0)
             (-> state
                 (assoc :level {:id level-id
                                :stars 0}))
             state)))

(defn update-choice-status [state choice-id]
      (let [prompt-id (get-in state [:level :question :prompt])
            result (= prompt-id choice-id)
            choices (get-in state [:level :question :choices])
            index (first (keep-indexed (fn [i choice]
                                           (when (= (choice :id) choice-id)
                                                 i))
                                       choices))]
           (assoc-in state [:level :question :choices index :correct?] result)))

(defn reset-character-mood [state]
      (set-no-actions-timeout!)
      (assoc-in state [:level :character-mood] :neutral))

(defn update-character-mood [state choice-id]
      (let [prompt-id (get-in state [:level :question :prompt :id])
            correct? (= prompt-id choice-id)]
           (assoc-in state [:level :character-mood] (if correct?
                                                      :happy
                                                      :angry))))

(defn back-to-levels [state]
      (-> state
          (assoc :level nil)
          (assoc :view :levels)))

(defn initialize [state level-id]
      (-> state
          (assoc :view :flash-game)
          (assoc :level {:id level-id
                         :progress 0})
          (reset-character-mood)
          (set-new-words)))

(reg-event-db
  :flash/back-to-levels
  (fn [state _]
      (back-to-levels state)))

(reg-event-db
  :flash/next-level
  (fn [state _]
      (let [level-id (get-in state [:level :id])
            episode-id (get-episode-id state level-id)
            level-ids (get-in state [:episodes episode-id :level-ids])
            next-level-id (->> level-ids
                               (drop-while (fn [id]
                                               (not= id level-id)))
                               second)]
           (if next-level-id
             (initialize state next-level-id)
             (back-to-levels state)))))

(reg-event-db
  :flash/reset-character-mood
  (fn [state _]
      (reset-character-mood state)))

(reg-event-db
  :flash/guess
  (fn [state [_ choice-id]]
      (-> state
          (update-choice-status choice-id)
          (update-character-mood choice-id)
          (update-when-correct choice-id)
          (maybe-set-win-state))))

(reg-event-db
  :flash/no-actions
  (fn [state _]
      (assoc-in state [:level :character-mood] :waiting)))

(reg-event-db
  :flash/initialize
  (fn [state [_ level-id]]
      (initialize state level-id)))

(reg-event-fx
  :flash/retry
  (fn [{state :db} _]
    {:dispatch [:flash/initialize (get-in state [:level :id])]}))
