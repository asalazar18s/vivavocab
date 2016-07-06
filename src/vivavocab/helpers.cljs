(ns vivavocab.helpers
  )

(defn get-episode-id [state level-id]
      (->> state
           :episodes
           vals
           (filter (fn [e]
                       (contains? (set (e :level-ids)) level-id)))
           first
           :id))
