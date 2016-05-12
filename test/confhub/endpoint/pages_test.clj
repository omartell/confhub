(ns confhub.endpoint.pages-test
  (:require [confhub.endpoint.pages :refer [pages-endpoint]]
            [confhub.system :as system]
            [confhub.config :refer [environ]]
            [confhub.test-setup :refer [setup-system *system*]]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]))

(use-fixtures :each setup-system)

(def page-config
  {:page {:id      :index
          :title   "Most popular news of 2016"
          :columns 3
          :ads     2}})

(defn parse-body [body]
  (some-> body
          slurp
          (json/read-str :key-fn keyword)))

(defn create-page-config [handler page-config]
  (-> (handler (-> (mock/request :post "/pages")
                   (mock/header "Content-Type" "application/json")
                   (mock/header "Accept" "application/json")
                   (mock/body (json/write-str page-config))))
      (update :body parse-body)))

(defn get-page-config [handler id]
  (-> (handler (-> (mock/request :get (str "/pages/" id))
                   (mock/header "Accept" "application/json")))
      (update :body parse-body)))

(defn update-page-config [handler id page-config]
  (-> (handler (-> (mock/request :put (str "/pages/" id))
                   (mock/header "Content-Type" "application/json")
                   (mock/header "Accept" "application/json")
                   (mock/body (json/write-str page-config))))
      (update :body parse-body)))

(defn delete-page-config [handler id]
  (-> (handler (-> (mock/request :delete "/pages/breaking-news")
                   (mock/header "Accept" "application/json")))
      (update :body parse-body)))

(deftest create-page-config-endpoint-test
  (let [handler (-> *system* :app :handler)]
    (testing "returns a successful response when the page config was created"
      (let [response (create-page-config handler page-config)]
        (is (= 201 (:status response)))
        (is (= {:id      "index"
                :columns 3
                :ads     2
                :title   "Most popular news of 2016"}
               (-> response :body :page)))
        (is (= {:self "http://localhost:3000/pages/index"}
               (-> response :body :links)))))

    (testing "returns an invalid response when the payload is not valid JSON"
      (let [response (handler (-> (mock/request :post "/pages")
                                  (mock/header "Content-Type" "application/json")
                                  (mock/header "Accept" "application/json")
                                  (mock/body "{id:\"editorial\"}")))]
        (is (= 422 (:status response)))))

    (testing "returns an invalid response when the page config already exists"
      (create-page-config handler (assoc page-config :id "weekly news"))

      (let [response (create-page-config handler
                                         (assoc page-config
                                                :id
                                                "weekly news"))]
        (is (= 422 (:status response)))))

    (testing "returns an unsuccessful response when there is missing data in the payload"
      (let [response (create-page-config handler (assoc-in page-config [:page :id] nil))]
        (is (= 422 (:status response)))
        (is (= {:error "Invalid data"}
               (-> response :body)))))))

(deftest get-page-config-test
  (let [handler (-> *system* :app :handler)]
    (testing "returns a sucessful response of an existing page config"
      (create-page-config handler (assoc-in page-config [:page :id] "top-news"))

      (let [response (get-page-config handler "top-news")]
        (is (= 200 (:status response)))
        (is (= {:id "top-news"
                :columns 3
                :ads 2
                :title "Most popular news of 2016"}
               (-> response :body :page)))
        (is (= {:self "http://localhost:3000/pages/top-news"}
               (-> response :body :links)))))

    (testing "returns a not found response when the page config does not exist"
      (let [response (get-page-config handler "not-found-news")]
        (is (= 404 (:status response)))
        (is (= {:error "Not Found"}
               (-> response :body)))))))

(deftest delete-page-config-test
  (let [handler (-> *system* :app :handler)]
    (testing "returns successful response when the page config was deleted"
      (create-page-config handler (assoc-in page-config [:page :id] "breaking-news"))

      (let [response (delete-page-config handler "breaking-news")]
        (is (= 200 (:status response)))
        (is (= {} (-> response :body)))))

    (testing "returns a not found response when the page config to be deleted does not exist"
      (let [response (delete-page-config handler "not-found-news")]
        (is (= 404 (:status response)))
        (is (= {:error "Not Found"}
               (-> response :body)))))))

(deftest update-page-config-test
  (let [handler (-> *system* :app :handler)]
    (testing "returns a successful response when the page config was updated"
      (create-page-config handler {:page {:id "sports-news" :title "Sporting events of 2016"}})
      (let [response (update-page-config handler
                                         "sports-news"
                                         {:page {:id    "sports-news"
                                                 :title "The best sporting events of 2016"}})]
        (is (= 200 (:status response)))
        (is (= {:self "http://localhost:3000/pages/sports-news"}
               (-> response :body :links)))))

    (testing "returns a successful response when the page config was updated with a new id"
      (create-page-config handler {:page {:id "weather-news" :title "Best places to visit in 2016"}})
      (let [response (update-page-config handler
                                         "weather-news"
                                         {:page {:id    "travel-news"
                                                 :title "The best sporting events of 2016"}})]
        (is (= 200 (:status response)))
        (is (= {:self "http://localhost:3000/pages/travel-news"}
               (-> response :body :links)))
        (is (= {:id "travel-news", :title "The best sporting events of 2016"}
               (-> (get-page-config handler "travel-news")
                   :body
                   :page)))))

    (testing "returns a not found response when the page config does not exist"
      (let [response (update-page-config handler
                                         "gossip-news"
                                         {:page {:id    "gossip-news"
                                                 :title "Celebrities gossips"}})]
        (is (= 404 (:status response)))
        (is (= {:error "Not Found"}
               (-> response :body)))))))
