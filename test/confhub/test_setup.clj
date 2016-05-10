(ns confhub.test-setup
  (:require  [clojure.java.jdbc :as jdbc]
             [confhub.config :refer [environ]]
             [com.stuartsierra.component :as component]
             [confhub.system :as system]))

(def ^:dynamic *system* nil)

(defn setup-system [f]
  (let [s (-> (system/new-system environ)
              (dissoc :http)
              component/start)]
    (binding [*system* s]
      (try
        (f)
        (finally
          (jdbc/execute! (-> s :db :spec) ["TRUNCATE page_configurations;"])
          (component/stop s))))))
