(ns utils.misc
  (:import (java.net InetAddress)))

(defn get-host
  []
  (let [local-host (InetAddress/getLocalHost)]
    {:host-name (.getHostName local-host)
     :host-address (.getHostAddress local-host)
     :canonical-host-name (.getCanonicalHostName local-host)}))
