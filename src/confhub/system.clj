(ns confhub.system
  (:require [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [confhub.endpoint.pages :refer [pages-endpoint]]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.component.ragtime :refer [ragtime]]
            [duct.component.hikaricp :refer [hikaricp]]
            [ring.middleware.resource :refer [wrap-resource]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [duct.middleware.route-aliases :refer [wrap-route-aliases]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.format :refer [wrap-restful-format]]))

(def base-config
  {:app {:middleware     [[wrap-restful-format :restful-format]
                          [wrap-resource :resources]
                          [wrap-route-aliases :aliases]]
         :resources   "confhub/public"
         :restful-format {:formats [:json-kw]}
         :aliases        {"/" "/index.html"}}
   :ragtime {:resource-path "confhub/migrations"}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :app  (handler-component (:app config))
         :http (jetty-server (:http config))
         :pages (endpoint-component pages-endpoint)
         :db   (hikaricp (:db config))
         :ragtime (ragtime (:ragtime config)))
        (component/system-using
         {:http    [:app]
          :app     [:pages]
          :ragtime [:db]
          :pages   [:db]}))))
