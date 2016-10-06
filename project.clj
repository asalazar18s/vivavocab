(defproject vivavocab "0.0.1"
  :dependencies [; server
                 [org.clojure/clojure "1.8.0"]
                 [http-kit "2.1.18"]
                 [compojure "1.5.1"]

                 ; client
                 [org.clojure/clojurescript "1.9.36"]
                 [re-frame "0.8.0"]
                 [garden "1.3.2"]
                 [prismatic/schema "1.1.2"]
                 [timothypratley/reanimated "0.3.0"]
                 [cljs-ajax "0.5.8"]]

  :plugins [[lein-figwheel "0.5.4-2"]]

  :figwheel {:server-port 3499}

  :source-paths ["src/server"]

  :main vivavocab.core

  :cljsbuild {:builds
              [{:id "dev"
                :figwheel {:on-jsload "vivavocab.core/reload"}
                :source-paths ["src/client"]
                :compiler {:main vivavocab.core
                           :asset-path "/js/dev"
                           :output-to "resources/public/js/dev.js"
                           :output-dir "resources/public/js/dev"
                           :verbose true}}]})
