(ns utils.io
  (:require (clojure.java [io :as io])))

(defn read-lines
  [file]
  (line-seq (io/reader file)))
