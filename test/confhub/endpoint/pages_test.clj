(ns confhub.endpoint.pages-test
  (:require [confhub.endpoint.pages :refer [pages-endpoint]]
            [confhub.system :as system]
            [confhub.config :refer [environ]]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]))

(def handler
  (-> (system/new-system environ)
      (dissoc :http)
      component/start
      :app
      :handler))

(deftest test-numerals-endpoint
  (testing "returns a successful representation when the number can be translated"
    (let [response (handler (-> (mock/request :post "/pages?zoo=1")
                                (mock/header "Content-Type" "application/json")
                                (mock/header "Accept" "application/json")
                                (mock/body (json/write-str {:page {:id      :index
                                                                   :title   "Most popular news of 2016"
                                                                   :columns 3
                                                                   :ads     2}}))))]
      (is (= 201 (:status response)))
      (is (= {:id "index"
              :columns 3
              :ads 2
              :title "Most popular news of 2016"}
             (-> response
                 :body
                 slurp
                 (json/read-str :key-fn keyword)
                 :page))))))
