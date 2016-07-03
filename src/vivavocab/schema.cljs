(ns vivavocab.schema
  (:require [schema.core :as s]))

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

