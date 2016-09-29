(ns vivavocab.core
    (:require
      [compojure.core :refer :all]
      [compojure.route :as route]
      [org.httpkit.server :refer [run-server]]))

(defroutes app
           (GET "/" [] "<h1>Hello World</h1>")
           (route/not-found "<h1>Page not found</h1>"))

(def server (atom nil))

(defn start! [port]
      (println "Starting web server on port" port)
      (reset! server (run-server #'app {:port port})))

(defn stop! []
      (@server))

(defn -main  [& args]
      (let [port (Integer/parseInt (first args))]
           (start! port)))
