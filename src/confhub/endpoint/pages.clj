(ns confhub.endpoint.pages
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [ring.util.response :refer [response status content-type]]
            [confhub.db :as db]))

(defn unknown-resource []
  (-> (response {:error "Not Found"})
      (status 404)
      (content-type "application/json")))

(defn get-page-config [db-spec id]
  (if-let [page-config (db/find-page-config db-spec id)]
    (response {:page page-config})
    (unknown-resource)))

(defn valid-page-config? [page-config]
  (and (not (empty? (:id page-config)))
       (not (empty? (dissoc page-config :id)))))

(defn create-page-config [db-spec page-config]
  (if (valid-page-config? page-config)
    (do
      (db/insert-page-config db-spec page-config)
      (-> (response {:page page-config})
          (status 201)))
    (-> (response {:error "Invalid data"})
        (status 422))))

(defn pages-endpoint [{{db-spec :spec} :db :as config}]
  (routes
   (GET "/pages/:id" [id] (get-page-config db-spec id))
   (POST "/pages" {{page :page} :params} (create-page-config db-spec page))
   (not-found (unknown-resource))))
