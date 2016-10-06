(ns vivavocab.handlers
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
            [vivavocab.games.flash.handlers :refer [set-new-words]]
            [ajax.core :refer [ajax-request]]
            [ajax.edn :refer [edn-response-format]]))

(defn key-by-id [items]
  (reduce (fn [memo item]
            (assoc memo (item :id) item)) {} items))

(reg-fx :ajax
        (fn [args]
          (ajax-request {:uri (args :uri)
                         :method (args :method)
                         :response-format (edn-response-format)
                         :handler (fn [[_ data]]
                                    (dispatch [(args :dispatch) data]))})))

(def initial-state
  {:episodes []
   :levels []
   :words []

   :key-options #{:text :translation :image}

   :view :levels ; :game :game-end :memory-game

   :level {:level-id 3
           :stars 0}})

(reg-event-fx
  :initialize
  (fn [{db :db} _]
    {:db (merge db initial-state)
     :dispatch [:fetch-data]}))

(reg-event-fx
  :fetch-data
  (fn [_ _]
    {:ajax {:uri "/api/data"
            :method :get
            :dispatch :process-data}}))

(reg-event-db
  :process-data
  (fn [state [_ data]]
    (assoc state
      :episodes (key-by-id (data :episodes))
      :levels (key-by-id (data :levels))
      :words (key-by-id (data :words)))))

(reg-event-fx
  :choose-level
  (fn [_ [_ level-id]]
    {:dispatch [:flash/initialize level-id]}))

(reg-event-fx
  :go-to-memory-game
  (fn [_ _]
    {:dispatch [:memory/initialize]}))



