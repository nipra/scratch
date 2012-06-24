(ns misc.jiraph-examples
  (:use [jiraph.graph])
  (:require [jiraph.core :as j])
  (:require  [jiraph.masai-layer]))

;;; Examples from https://github.com/flatland/jiraph

(def g
  {:foo (jiraph.masai-layer/make "/tmp/foo")
   :bar (jiraph.masai-layer/make "/tmp/bar")
   :baz (jiraph.masai-layer/make "/tmp/baz")})

(comment
  (j/with-graph g
    (j/add-node! :foo "human-1" {:name "Justin"  :edges {"human-2" {:type :spouse}}})
    (j/add-node! :foo "human-2" {:name "Heather" :edges {"human-1" {:type :spouse}}})

    (j/get-node :foo "human-1")))
