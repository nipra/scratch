(ns scratch.main
  (:gen-class)
  (:require [swank.swank]))

(defn -main
  []
  (swank.swank/start-server :port 6969))
