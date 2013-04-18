(ns ^{:doc "Original sources for code written here:
   1. https://github.com/overtone/overtone/blob/master/src/overtone/helpers/seq.clj
   2. https://github.com/flatland/useful/blob/develop/src/flatland/useful/seq.clj
   3. /me"}
  utils.seq
  (:import (java.util Collection)))

;;; 
(defn find-first
  "Finds first element of seq `s' for which `pred' returns true"
  [pred s]
  (first (filter pred s)))

(defn member?
  [elem coll]
  (.contains ^Collection coll elem))

;;; 
(defn include?
  "Check if `val' exists in `coll'."
  [val coll]
  (some (partial = val) coll))

;;; 
(defn kv-pairs->map
  "input => [[:a 1] [:b 2] [:a 2] [:c 3]]
   output => {:a [1 2] :b [2] :c [3]}"
  [kv-pairs]
  (reduce (fn [result x]
            (update-in result [(first x)] conj (second x)))
          {}
          kv-pairs))
