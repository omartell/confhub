(ns confhub.endpoint.pages
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response]]))

(defn pages-endpoint [config]
  (POST "/pages" {{page :page :as params} :params :as request}
    (response {:id "foo"})))
