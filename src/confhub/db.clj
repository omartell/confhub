(ns confhub.db
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.data.json :as json])
  (:import org.postgresql.util.PGobject))

(defn- map->pgjson [object]
  (doto (PGobject.)
    (.setType "json")
    (.setValue (json/write-str object))))

(defn- pgjson->map [pgobject]
  (json/read-str (.getValue pgobject) :key-fn keyword))

(defn insert-page-config [spec page-config]
  (jdbc/insert! spec
                :page_configurations
                {:page_id (:id page-config)
                 :configuration (map->pgjson page-config)}))

(defn find-page-config [spec id]
  (some-> (jdbc/query spec
                      ["SELECT * FROM page_configurations WHERE page_id = ?" id])
          first
          :configuration
          pgjson->map))
