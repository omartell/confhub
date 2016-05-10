(ns confhub.endpoint.pages-test
  (:require [confhub.endpoint.pages :refer [pages-endpoint]]
            [confhub.system :as system]
            [confhub.config :refer [environ]]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]))

(defn parse-body [response]
  (-> response
      :body
      slurp
      (json/read-str :key-fn keyword)))

(deftest test-numerals-endpoint
  (let [system  (-> (system/new-system environ)
                    (dissoc :http)
                    component/start)
        handler (-> system :app :handler)]

    (testing "returns a successful representation when the number can be translated"
      (let [response (handler (-> (mock/request :post "/pages?zoo=1")
                                  (mock/header "Content-Type" "application/json")
                                  (mock/header "Accept" "application/json")
                                  (mock/body (json/write-str {:page {:id      :index
                                                                     :title   "Most popular news of 2016"
                                                                     :columns 3
                                                                     :ads     2}}))))]
        (is (= 201 (:status response)))
        (is (= {:id      "index"
                :columns 3
                :ads     2
                :title   "Most popular news of 2016"}
               (-> response
                   parse-body
                   :page)))))

    (testing "returns a not found response when the config does not exist"
      (let [response (handler (-> (mock/request :post "/pages/top-news")
                                  (mock/header "Accept" "application/json")))]
        (is (= 404 (:status response)))
        (is (= "application/json; charset=utf-8" (-> response :headers (get "Content-Type"))))
        (is (= {:error "Not Found"}
               (-> response
                   parse-body)))))

    (component/stop system)))
