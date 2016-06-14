(defproject vivavocab "0.0.1"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.36"]
                 [re-frame "0.7.0"]
                 [garden "1.3.2"]
                 [prismatic/schema "1.1.2"]]

  :plugins [[lein-figwheel "0.5.4-2"]]

  :figwheel {:server-port 3499}

  :cljsbuild {:builds
              [{:id "dev"
                :figwheel true
                :source-paths ["src"]
                :compiler {:main vivavocab.core
                           :asset-path "/js/dev"
                           :output-to "resources/public/js/dev.js"
                           :output-dir "resources/public/js/dev"
                           :verbose true}}]})
