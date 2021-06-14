(ns reverse-proxy.proxy
  (:require [clj-http.client :as http]
            [reverse-proxy.storage :as storage]))

(def cm (clj-http.conn-mgr/make-reusable-conn-manager {:timeout 10 :threads 4}))

(defn proxy-key [uri]
  (clojure.string/replace uri #"/([^/]+)/.*" "$1"))

(defn proxy-path [uri]
  (clojure.string/replace uri #"/[^/]+(/.*)" "$1")) 

(defn pipe [request]
  (let [{method :request-method path :uri} request
        key (proxy-key path)
        path' (proxy-path path)
        backend (storage/findBackend key)
        response (http/request {:method method
                                :url (str backend path)
                                :connection-manager cm
                                :stream true
                                :throw-exceptions false})
        {:keys [status headers body]} response]
    {:status status
     :headers headers
     :body body}))
