(ns confhub.endpoint.response
  (:require [ring.util.response :refer [response status]]))

(defn links [app-config page-config]
  {:self (str "http://"
              (-> app-config :links :host)
              "/pages/"
              (:id page-config))})

(defn updated [app-config page-config]
  (-> (response {:links (links app-config page-config)})
      (status 200)))

(defn success
  ([]
   (-> (response {})
       (status 200)))
  ([app-config page-config]
   (-> (response {:links (links app-config page-config)
                  :page page-config})
       (status 200))))

(defn created [app-config page-config]
  (-> (response {:page page-config
                 :links (links app-config page-config)})
      (status 201)))

(defn not-found []
  (-> (response {:error "Not Found"})
      (status 404)))

(defn invalid []
  (-> (response {:error "Invalid data"})
      (status 422)))
