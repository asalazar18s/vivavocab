(ns vivavocab.middleware
  (:require [re-frame.core :refer [after]]
            [vivavocab.schema :as schema]))

(def middleware
  [(after schema/valid-schema?)])
