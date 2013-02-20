(ns hbase.init
  (:require (libs.clojure-hbase [core :as hb]
                                [util :as hu]
                                [admin :as ha])))


(defonce local-db
  (atom (hb/htable-pool* {"hbase.zookeeper.quorum" "localhost"
                          "hbase.rootdir" "hdfs://localhost:8020/hbase"})))

(defonce local-db-admin
  (atom (ha/hbase-admin*  {"hbase.zookeeper.quorum" "localhost"
                           "hbase.rootdir" "hdfs://localhost:8020/hbase"})))


(defmacro with-local
  [& body]
  `(binding [hb/*db* production-db]
     ~@body))
