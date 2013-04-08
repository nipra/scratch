(ns utils.net
  (:import (java.net URLEncoder)))

(defn url-encode
  ([url]
     (url-encode url "UTF-8"))

  ([url encoding-scheme]
     (URLEncoder/encode url encoding-scheme)))
