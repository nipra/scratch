(ns utils.string
  (:import (java.util.regex Pattern))
  (:require (clojure [string :as s])))

(defn re-quote
  [string]
  (Pattern/quote string))
