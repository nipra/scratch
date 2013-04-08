(ns utils.string
  (:import (java.util.regex Pattern))
  (:import (java.nio.charset Charset))
  (:require (clojure [string :as s])))

;;; http://www.iana.org/assignments/character-sets/character-sets.xml
;;; http://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html
(defonce charsets {:US-ASCII "US-ASCII"
                   :ISO-8859-1 "ISO-8859-1"
                   :UTF-8 "UTF-8"
                   :UTF-16BE "UTF-16BE"
                   :UTF-16LE "UTF-16LE"
                   :UTF-16 "UTF-16"})

(defonce utf8 (:UTF-8 charsets))

(defn re-quote
  [string]
  (Pattern/quote string))
