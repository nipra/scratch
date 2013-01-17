(ns hbase.count
  (:require (libs.clojure-hbase [core :as hb]
                                [util :as hu]
                                [admin :as ha]))
  (:require (hbase [filters :as f]))
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

(defn count-rows-with-filters
  [table-name filters]
  (hb/with-table [table (hb/table table-name)]
    (hb/with-scanner [scanner (hb/scan table
                                       :caching 100000
                                       :filter (if (instance? FilterList filters)
                                                 filters
                                                 (FilterList. filters)))]
      (loop [n 0]
        (let [result (.next scanner 10000)
              num-result (count result)]
          (if (seq result)
            (recur (+ n num-result))
            n))))))


(defn count-rows-with-column
  [table-name column-family qualifier]
  (let [filters [(f/qualifier-filter qualifier) (f/family-filter column-family)]]
    (count-rows-with-filters table-name filters)))


(defn count-rows-with-suffix
  "WARNING: Uses RegexStringComparator, so be sure to escape characters with special meaning in a regex."
  [table-name suffix]
  (let [row-filter (f/row-filter-with-regex (format "^.*%s$" suffix))]
    (count-rows-with-filters table-name [row-filter])))


(defn count-rows
  [table-name start-row end-row & {:keys [caching batch] :or {caching 1000 batch 100}}]
  (hb/with-table [table (hb/table table-name)]
    (hb/with-scanner [scanner (hb/scan table
                                       :start-row start-row
                                       :stop-row end-row
                                       :caching caching)]
      (loop [n 0]
        (let [results (.next scanner batch)]
          (if (seq results)
            (recur (+ n (count results)))
            n))))))
