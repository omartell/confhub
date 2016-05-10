(ns confhub.endpoint.pages
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [ring.util.response :refer [response status content-type]]
            [confhub.db :as db]))

(defn unknown-resource []
  (-> (response {:error "Not Found"})
      (status 404)
      (content-type "application/json")))

(defn pages-endpoint [{{db-spec :spec} :db :as config}]
  (routes
   (GET "/pages/:id" [id]
        (if-let [page-config (db/find-page-config db-spec id)]
          (response {:page page-config})
          (unknown-resource)))
   (POST "/pages" {{page :page} :params}
         (db/insert-page-config db-spec page)
         (-> (response {:page page})
             (status 201)))
   (not-found (unknown-resource))))
