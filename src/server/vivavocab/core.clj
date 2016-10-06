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
           (GET "/api/data" []
             {:headers {"Content-Type" "application/edn"}
              :body (-> "data.edn"
                        io/resource
                        io/file
                        slurp)})
           (route/resources "/")
           (route/not-found "<h1>Page not found</h1>"))

(defonce server (atom nil))

(defn start! [port]
  (when @server
    (@server))
  (println "Starting web server on port" port)
  (reset! server (run-server #'app {:port port}))
  nil)

(defn -main  [& args]
      (let [port (Integer/parseInt (first args))]
           (start! port)))
