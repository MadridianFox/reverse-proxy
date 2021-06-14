(ns reverse-proxy.core
  (:use [ring.adapter.jetty]
        [reverse-proxy.storage :as storage]
        [reverse-proxy.proxy :as proxy])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(defroutes app
  (-> (context "/api-proxy" []
           (GET "/routes" []
                {:status 200 :body (storage/getProxyList)})
           (POST "/routes" request
                 (let [params  (:params request)
                       backend (:backend params)
                       id      (:id params)]
                     (storage/addProxy)
                     {:status 200 :body {:status "done"}})))
      wrap-keyword-params
      wrap-json-params     
      wrap-json-response)
  (ANY "/*" request
       (proxy/pipe request)))

(comment
  (def server (run-jetty #'app {:port  3000
                                :join? false}))
  )
