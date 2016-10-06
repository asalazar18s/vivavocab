(ns vivavocab.handlers
  (:require [re-frame.core :refer [register-handler dispatch]]
            [vivavocab.games.flash.handlers :refer [set-new-words]]
            [ajax.core :refer [ajax-request]]
            [ajax.edn :refer [edn-response-format]]))

(def initial-state
  {:episodes []
   :levels []
   :words []

   :key-options #{:text :translation :image}

   :view :levels ; :game :game-end :memory-game

   :level {:level-id 3
           :stars 0}})

(defn key-by-id [items]
  (reduce (fn [memo item]
            (assoc memo (item :id) item)) {} items))

(register-handler
  :initialize
  (fn [state _]
    (ajax-request {:uri "/api/data"
                   :method :get
                   :response-format (edn-response-format)
                   :handler (fn [[_ data]]
                              (dispatch [:process-data data]))})
    (merge state initial-state)))

(register-handler
  :process-data
  (fn [state [_ data]]
    (assoc state
      :episodes (key-by-id (data :episodes))
      :levels (key-by-id (data :levels))
      :words (key-by-id (data :words)))))

(register-handler
  :choose-level
  (fn [state [_ level-id]]
      (dispatch [:flash/initialize level-id])
      state))

(register-handler
  :go-to-memory-game
  (fn [state _]
      (dispatch [:memory/initialize])
      state))



