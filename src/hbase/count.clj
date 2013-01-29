(ns hbase.count
  (:require (libs.clojure-hbase [core :as hb]
                                [util :as hu]
                                [admin :as ha]))
  (:require (hbase [utils :as u] [filters :as f]))
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
                                           RegexStringComparator)))
;;; TODO:
;;; * Explore KeyOnlyFilter for better performance

(defn count-rows-with-filters
  [table-name filters]
  (hb/with-table [table (hb/table table-name)]
    (hb/with-scanner [scanner (hb/scan table
                                       :caching 100000
                                       :filter (f/sanitize-filters filters true))]
      (loop [n 0]
        (let [result (.next scanner 10000)
              num-result (count result)]
          (if (seq result)
            (recur (+ n num-result))
            n))))))


(defn count-rows-with-column
  [table-name column-family qualifier]
  (let [filters [(f/column-qualifier-filter qualifier)
                 (f/column-family-filter column-family)]]
    (count-rows-with-filters table-name filters)))


(defn count-rows-with-suffix
  "WARNING: Uses RegexStringComparator, so be sure to escape characters with 
   special meaning in a regex."
  [table-name suffix]
  (let [row-filter (f/row-filter-with-regex (format "^.*%s$" suffix))]
    (count-rows-with-filters table-name [row-filter])))


(defn count-rows
  [table-name start-row end-row & {:keys [caching batch filters key-only?]
                                   :or {caching 1000
                                        batch 100
                                        key-only? true}}]
  (hb/with-table [table (hb/table table-name)]
    (hb/with-scanner [scanner (u/scan* table
                                       :start-row start-row
                                       :stop-row end-row
                                       :caching caching
                                       :filter (f/sanitize-filters filters key-only?))]
      (loop [n 0]
        (let [results (.next scanner batch)]
          (if (seq results)
            (recur (+ n (count results)))
            n))))))

(comment
  (let [column-filter (f/column-filter "column-family" "column-qualifier")]
    (count-rows "table-name" "start-row" "stop-row"
                :row-as u/result->key
                :filters [column-filter])))
