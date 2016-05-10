(ns confhub.endpoint.pages-test
  (:require [confhub.endpoint.pages :refer [pages-endpoint]]
            [confhub.system :as system]
            [confhub.test-setup :refer [setup-system *system*]]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]))

(def page-config
  {:page {:id      :index
          :title   "Most popular news of 2016"
          :columns 3
          :ads     2}})

(defn parse-body [response]
  (-> response
      :body
      slurp
      (json/read-str :key-fn keyword)))

(defn create-page-config [handler page-config]
  (handler (-> (mock/request :post "/pages")
               (mock/header "Content-Type" "application/json")
               (mock/header "Accept" "application/json")
               (mock/body (json/write-str page-config)))))

(use-fixtures :once setup-system)

(deftest test-pages-endpoint
  (let [handler (-> *system* :app :handler)]

    (testing "returns a successful representation when the number can be translated"
      (let [response (create-page-config handler page-config)]
        (is (= 201 (:status response)))
        (is (= {:id      "index"
                :columns 3
                :ads     2
                :title   "Most popular news of 2016"}
               (-> response
                   parse-body
                   :page)))))

    (testing "returns a response of a previously created page configuration"
      (create-page-config handler (assoc-in page-config [:page :id] "top-news"))

      (let [response (handler (-> (mock/request :get "/pages/top-news")
                                  (mock/header "Accept" "application/json")))]
        (is (= 200 (:status response)))
        (is (= {:id "top-news"
                :columns 3
                :ads 2
                :title "Most popular news of 2016"}
               (-> response
                   parse-body
                   :page)))))

    (testing "returns validation errors when there is missing data in the config"
      (let [response (create-page-config handler (assoc-in page-config [:page :id] nil))]
        (is (= 422 (:status response)))
        (is (= {:error "Invalid data"}
               (-> response
                   parse-body)))))

    (testing "returns a not found response when the config does not exist"
      (let [response (handler (-> (mock/request :get "/pages/old-news")
                                  (mock/header "Accept" "application/json")))]
        (is (= 404 (:status response)))
        (is (= {:error "Not Found"}
               (-> response
                   parse-body)))))

    (testing "returns successful response when deleting an existing page configuration"
      (create-page-config handler (assoc-in page-config [:page :id] "breaking-news"))

      (let [response (handler (-> (mock/request :delete "/pages/breaking-news")
                                  (mock/header "Accept" "application/json")))]
        (is (= 200 (:status response)))))

    (testing "returns not found response when deleting missing page configuration"
      (let [response (handler (-> (mock/request :delete "/pages/finance-news")
                                  (mock/header "Accept" "application/json")))]
        (is (= 404 (:status response)))
        (is (= {:error "Not Found"}
               (-> response
                   parse-body)))))))
