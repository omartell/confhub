            (ns confhub.endpoint.pages-test
              (:require [confhub.endpoint.pages :refer [pages-endpoint]]
                        [confhub.system :as system]
                        [ring.mock.request :as mock]
                        [clojure.data.json :as json]
                        [com.stuartsierra.component :as component]
                        [clojure.test :refer :all]))

(def handler
  (-> (system/new-system {})
      (dissoc :http)
      component/start
      :app
      :handler))

(deftest test-numerals-endpoint
  (testing "returns a successful representation when the number can be translated"
    (is (= {:status  201
            :body    {}}
           (-> (handler (-> (mock/request :post "/pages?zoo=1")
                            (mock/header "Content-Type" "application/json")
                            (mock/header "Accept" "application/json")
                            (mock/body (json/write-str {:page {:id :index
                                                               :title "Most popular news of 2016"
                                                               :columns 3
                                                               :ads 2}}))))
               :body
               slurp
               json/read-str)))))
