(ns utils.classpath
  (:require [clojure.java.classpath :as cp]))

(defn get-classpath-jar-files
  []
  (map #(.getName %) (cp/classpath-jarfiles)))
