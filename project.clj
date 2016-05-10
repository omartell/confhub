(defproject confhub "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.postgresql/postgresql "9.4.1207"]
                 [org.slf4j/slf4j-nop "1.7.14"]
                 [com.stuartsierra/component "0.3.1"]
                 [environ "1.0.2"]
                 [meta-merge "0.1.1"]
                 [hanami "0.1.0"]
                 [honeysql "0.6.3"]
                 [duct "0.5.10"]
                 [duct/ragtime-component "0.1.3"]
                 [duct/hikaricp-component "0.1.0"]
                 [ring "1.4.0"]
                 [compojure "1.5.0"]
                 [ring-middleware-format "0.7.0"]
                 [ring/ring-mock "0.3.0"]
                 [ring-jetty-component "0.3.1"]]
  :plugins [[lein-environ "1.0.2"]
            [lein-gen "0.2.2"]]
  :generators [[duct/generators "0.5.10"]]
  :duct {:ns-prefix confhub}
  :main ^:skip-aot confhub.main
  :uberjar-name "confhub-standalone.jar"
  :target-path "target/%s/"
  :aliases {"migrate"  ["run" "-m" "user/start-system-and-migrate"]
            "rollback" ["run" "-m" "user/start-system-and-rollback"]
            "gen"   ["generate"]
            "setup" ["do" ["generate" "locals"]]
            "deploy" ["do"
                      ["vcs" "assert-committed"]
                      ["vcs" "push" "heroku" "master"]]}
  :profiles
  {:dev  [:project/dev  :profiles/dev]
   :test [:project/test :profiles/test]
   :uberjar {:aot :all}
   :profiles/dev  {}
   :profiles/test {}
   :project/dev   {:env {:database-url "postgres://localhost/confhub_development"
                         :port "3000"}
                   :dependencies [[reloaded.repl "0.2.1"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [eftest "0.1.1"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}}
   :project/test  {:env {:database-url "postgres://localhost/confhub_test"
                         :port "3000"}
                   :dependencies [[reloaded.repl "0.2.1"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [eftest "0.1.1"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}}})
