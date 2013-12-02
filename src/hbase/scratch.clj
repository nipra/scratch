(ns hbase.scratch
  (:gen-class)
  (:require [libs.clojure-hbase [core :as hb] [util :as hu] [admin :as ha]])
  ;; (:require [libs.com.compass.hbase.client :as c])
  (:require [clojure.java.classpath :as cp])
  (:import [java.util Set]
           [org.apache.hadoop.hbase HBaseConfiguration HConstants KeyValue]
           (org.apache.hadoop.hbase.client HTable
                                           HTablePool Get Put Delete Scan Result RowLock)
           [org.apache.hadoop.hbase.util Bytes])
  (:import (org.apache.hadoop.hbase.filter ValueFilter
                                           CompareFilter
                                           CompareFilter$CompareOp
                                           WritableByteArrayComparable
                                           BinaryComparator
                                           SingleColumnValueFilter
                                           PrefixFilter
                                           QualifierFilter
                                           FamilyFilter
                                           FilterList
                                           RowFilter
                                           RegexStringComparator))
  (:require [clojure.pprint :as p]))

(comment
  (ha/hbase-admin)
  (ha/list-tables))

(comment
  (count
   (hb/with-table [table (hb/table "table")]
     (hb/with-scanner [scanner (hb/scan table
                                        :start-row "start-row"
                                        :stop-row "stop-row"
                                        :caching 1000)]
       (loop [x []]
         (let [result (.next scanner 100)]
           (if (seq result)
             (recur (doall (concat x result)))
             x))))))

  (hb/with-table [table (hb/table "table")]
    (hb/with-scanner [scanner (hb/scan table
                                       :start-row "start-row"
                                       :stop-row "stop-row"
                                       :caching 1000000)]
      (loop [n 0]
        (let [result (.next scanner 100000)]
          (if (seq result)
            (recur (+ n (count result)))
            n)))))

  (count
   (hb/with-table [table (hb/table "table")]
     (hb/with-scanner [scanner (hb/scan table
                                        :start-row "start-row"
                                        :stop-row "stop-row")]
       (loop [x []]
         (let [result (.next scanner)]
           (if result
             (recur (doall (conj x (hb/as-vector result
                                                 :map-value hu/as-str
                                                 :map-family hu/as-str
                                                 :map-qualifier hu/as-str))))
             x)))))))
