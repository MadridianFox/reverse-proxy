(ns reverse-proxy.stub
  (:use [ring.adapter.jetty]))

(def handler (-> (fn [request]
                   {:status 200 :body (str
                                       (:request-method request) " " (:uri request) " "  (:query-string request) " " (:protocol request)
                                       "\n"
                                       (clojure.string/join "\n" (map (fn [[name value]] (str name ": " value)) (:headers request)))
                                       "\n---\n\n"
                                       (slurp (:body request)))})
                 wrap-json-response))

(defn -main [& args]
  (run-jetty #'handler {:port 8080
                        :join? false}))

(comment
  (-main)
  )
