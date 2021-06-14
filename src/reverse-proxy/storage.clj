(ns reverse-proxy.storage)

(defonce *proxies (atom {}))

(defn getProxyList []
  @*proxies)

(defn addProxy [id options]
  (swap! *proxies assoc id options))

(defn findBackend [key]
  (or (get @*proxies key)
      (get @*proxies "default")))
