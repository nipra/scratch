(ns utils.net
  (:import (java.net URLEncoder URLDecoder)))

(defn url-encode
  [url & [encoding-scheme]]
  (URLEncoder/encode url (or encoding-scheme "UTF-8")))

(defn url-decode
  [url & [encoding-scheme]]
  (URLDecoder/decode url (or encoding-scheme "UTF-8")))

(defn is-encoded?
  [url & [encoding-scheme]]
  (not= (url-decode url encoding-scheme)
        url))
