(ns vivavocab.core
    (:require
      [compojure.core :refer :all]
      [compojure.route :as route]
      [org.httpkit.server :refer [run-server]]
      [clojure.java.io :as io]))

(defroutes app
           (GET "/" []
                (-> "public/index.html"
                    io/resource
                    io/file
                    slurp))
           (GET "/api/data" [] ) ;TODO return game data
           (route/resources "/")
           (route/not-found "<h1>Page not found</h1>"))

(defonce server (atom nil))

(defn start! [port]
      (println "Starting web server on port" port)
      (reset! server (run-server #'app {:port port})))

(defn stop! []
      (@server))

(defn -main  [& args]
      (let [port (Integer/parseInt (first args))]
           (start! port)))
