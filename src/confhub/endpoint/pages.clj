(ns confhub.endpoint.pages
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [confhub.endpoint.response :as response]
            [confhub.db :as db]))

(defn valid-page-config? [page-config]
  (and (not (empty? (:id page-config)))
       (not (empty? (dissoc page-config :id)))))

(defn get-page-config [config db-spec id]
  (if-let [page-config (db/find-page-config db-spec id)]
    (response/success config page-config)
    (response/not-found)))

(defn create-page-config [config db-spec page-config]
  (if (valid-page-config? page-config)
    (try
      (db/insert-page-config db-spec page-config)
      (response/created config page-config)
      (catch org.postgresql.util.PSQLException e
        (response/invalid)))
    (response/invalid)))

(defn update-page-config [config db-spec existing-id page-config]
  (if (valid-page-config? page-config)
    (let [result (db/update-page-config db-spec existing-id page-config)]
      (if (> (first result) 0)
        (response/updated config page-config)
        (response/not-found)))
    (response/invalid)))

(defn delete-page-config [db-spec id]
  (let [result (db/delete-page-config db-spec id)]
    (if (> (first result) 0)
      (response/success)
      (response/not-found))))

(defn pages-endpoint [{{db-spec :spec} :db config :endpoint :as system}]
  (routes
   (POST "/pages" {{page :page} :params}
         (create-page-config config db-spec page))
   (PUT "/pages/:id" {{page :page :as params} :params}
        (update-page-config config db-spec (:id params) page))
   (GET "/pages/:id" [id]
        (get-page-config config db-spec id))
   (DELETE "/pages/:id" [id]
           (delete-page-config db-spec id))
   (not-found (response/not-found))))
