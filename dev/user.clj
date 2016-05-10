(ns user
  (:require [clojure.repl :refer :all]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [eftest.runner :as eftest]
            [meta-merge.core :refer [meta-merge]]
            [reloaded.repl :refer [system init start stop go reset]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [duct.component.ragtime :as ragtime]
            [confhub.config :as config]
            [confhub.system :as system]))

(def dev-config
  {:app {:middleware [wrap-stacktrace]}})

(def config
  (meta-merge config/defaults
              config/environ
              dev-config))

(defn new-system []
  (into (system/new-system config)
        {}))

(ns-unmap *ns* 'test)

(defn test []
  (eftest/run-tests (eftest/find-tests "test") {:multithread? false}))

(defn migrate
  ([] (migrate system))
  ([s] (-> s :ragtime ragtime/reload ragtime/migrate)))

(defn rollback
  ([]  (rollback system 1))
  ([s x] (-> s :ragtime ragtime/reload (ragtime/rollback x))))

(defn start-system-and-migrate []
  (let [s (-> (new-system)
              (dissoc :http)
              (component/start))]
    (migrate s)))

(defn start-system-and-rollback []
  (let [s (-> (new-system)
              (dissoc :http)
              (component/start))]
    (rollback s 1)))

(when (io/resource "local.clj")
  (load "local"))

(reloaded.repl/set-init! new-system)
