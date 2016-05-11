(ns confhub.endpoint.pages
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [ring.util.response :refer [response status content-type]]
            [confhub.db :as db]))

(defn success-response [body]
  (-> (response body)
      (status 200)))

(defn links [app-config page-config]
  {:self (str "http://" (-> app-config :links :host) "/pages/" (:id page-config))})

(defn created-response [app-config page-config]
  (-> (response {:page page-config
                 :links (links app-config page-config)})
      (status 201)))

(defn not-found-response []
  (-> (response {:error "Not Found"})
      (status 404)))

(defn invalid-response []
  (-> (response {:error "Invalid data"})
      (status 422)))

(defn valid-page-config? [page-config]
  (and (not (empty? (:id page-config)))
       (not (empty? (dissoc page-config :id)))))

(defn get-page-config [app-config db-spec id]
  (if-let [page-config (db/find-page-config db-spec id)]
    (response {:page  page-config
               :links (links app-config page-config)})
    (not-found-response)))

(defn create-page-config [app-config db-spec page-config]
  (if (valid-page-config? page-config)
    (do
      (db/insert-page-config db-spec page-config)
      (created-response app-config page-config))
    (invalid-response)))

(defn update-page-config [app-config db-spec existing-id page-config]
  (if (valid-page-config? page-config)
    (let [result (db/update-page-config db-spec existing-id page-config)]
      (if (> (first result) 0)
        (success-response {:links (links app-config page-config)})
        (not-found-response)))
    (invalid-response)))

(defn delete-page-config [db-spec id]
  (let [result (db/delete-page-config db-spec id)]
    (if (> (first result) 0)
      (success-response {})
      (not-found-response))))

(defn pages-endpoint [{{db-spec :spec} :db app-config :config :as system}]
  (routes
   (POST "/pages" {{page :page} :params}
         (create-page-config app-config db-spec page))
   (PUT "/pages/:id" {{page :page :as params} :params}
        (update-page-config app-config db-spec (:id params) page))
   (GET "/pages/:id" [id]
        (get-page-config app-config db-spec id))
   (DELETE "/pages/:id" [id]
           (delete-page-config db-spec id))
   (not-found (not-found-response))))
