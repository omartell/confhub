(ns confhub.endpoint.pages
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response status]]))

(defn pages-endpoint [config]
  (POST "/pages" {{page :page} :params}
        (-> (response {:page page})
            (status 201))))
