(ns confhub.endpoint.response
  (:require [ring.util.response :refer [response status]]))

(defn links [config page-config]
  {:self (str "http://"
              (:host config)
              "/pages/"
              (:id page-config))})

(defn updated [config page-config]
  (-> (response {:links (links config page-config)})
      (status 200)))

(defn success
  ([]
   (-> (response {}) (status 200)))
  ([config page-config]
   (-> (response {:links (links config page-config)
                  :page  page-config})
       (status 200))))

(defn created [config page-config]
  (-> (response {:page  page-config
                 :links (links config page-config)})
      (status 201)))

(defn not-found []
  (-> (response {:error "Not Found"})
      (status 404)))

(defn invalid []
  (-> (response {:error "Invalid data"})
      (status 422)))
