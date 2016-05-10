(ns confhub.endpoint.pages
  (:require [compojure.core :refer :all]
            [compojure.route :refer [not-found]]
            [clojure.data.json :as json]
            [ring.util.response :refer [response status content-type]]))

(defn pages-endpoint [config]
  (routes
   (POST "/pages" {{page :page} :params}
         (-> (response {:page page})
             (status 201)))
   (not-found (-> (response {:error "Not Found"})
                  (content-type "application/json")))))
