(ns hbase.utils
  (:gen-class)
  (:require [libs.clojure-hbase [core :as hb] [util :as hu] [admin :as ha]])
  (:require [libs.com.compass.hbase.client :as c])
  (:require (clj-time [core :as ctc]
                      [format :as ctf]
                      [coerce :as ctco]
                      [local :as ctl]))
  (:require [clojure.java.classpath :as cp])
  (:import (org.apache.hadoop.fs Path))
  (:import [java.util Set]
           (org.apache.hadoop.hbase HBaseConfiguration
                                    HTableDescriptor
                                    HConstants
                                    KeyValue
                                    TableNotFoundException)
           (org.apache.hadoop.hbase.client HTable
                                           HTablePool
                                           HBaseAdmin
                                           RowLock
                                           Get Put Delete Scan Result)
           [org.apache.hadoop.hbase.util Bytes])
  (:require [clojure.pprint :as p]))

;;  A file called hbase-site.xml lives in the conf directory inside the HBase
;; root folder. This file will need to be copied into a location on the JVM’s
;; classpath and will need to contain some information regarding your
;; particular installation of HBase.

;;; (nprabhak@unmac ~)$ cp hbase_installation/hbase/conf/hbase-site.xml Projects/Clojure/scratch/resources/

(defn hbase-table [table-name]
  (HTable. (HBaseConfiguration.) table-name))

(defn add-to-put [p object column-family]
  (let [name-of (fn [x]
                  (if (keyword? x) (name x) (str x)))]
    (doseq [[k v] object]
      (.add p (Bytes/toBytes column-family)
            (Bytes/toBytes (name-of k))
            (Bytes/toBytes (str v))))))

(defn put-in-table [object table-name column-family row-id]
  (let [table (hbase-table table-name)
        p (Put. (Bytes/toBytes row-id))]
    (add-to-put p object column-family)
    (.put table p)))

(defn print-from-table [table-name row-id column-family]
  (let [table (hbase-table table-name)
        g (Get. (Bytes/toBytes row-id))
        r (.get table g)
        nm (.getFamilyMap r (Bytes/toBytes column-family))]
    (doseq [[k v] nm]
      (println (String. k) ":" (String. v)))))

;;;
(defn to-long
  [x]
  (Bytes/toLong x))

(defn result->key
  [result]
  (hu/as-str (.getRow result)))

(defn as-vector
  [result]
  (map #(cons (result->key result) %)
       (hb/as-vector result
                     :map-value hu/as-str
                     :map-family hu/as-str
                     :map-qualifier hu/as-str
                     :map-timestamp ctco/from-long)))

(defn as-map
  [result]
  (hb/as-map result
             :map-value hu/as-str
             :map-family hu/as-str
             :map-qualifier hu/as-str
             :map-timestamp ctco/from-long))

(defn latest-as-map
  [result]
  (hb/latest-as-map result
                    :map-value hu/as-str
                    :map-family hu/as-str
                    :map-qualifier hu/as-str))
