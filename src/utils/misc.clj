(ns utils.misc
  (:import (java.net InetAddress))
  (:import (java.util UUID)))

(defn get-host
  []
  (let [local-host (InetAddress/getLocalHost)]
    {:host-name (.getHostName local-host)
     :host-address (.getHostAddress local-host)
     :canonical-host-name (.getCanonicalHostName local-host)}))

(defn sleep
  [time]
  "``time'' in ms"
  (Thread/sleep time))

;;; http://corfield.org/blog/post.cfm/to-uuid-or-not-to-uuid
;;; http://johannburkard.de/blog/programming/java/Java-UUID-generators-compared.html
(defn uuid
  []
  (str (java.util.UUID/randomUUID)))
