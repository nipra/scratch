(ns hbase.query
  (:require (libs.clojure-hbase [core :as hb]
                                [util :as hu]
                                [admin :as ha]))
  (:require (hbase [utils :as utils] [filters :as f]))
  (:import (org.apache.hadoop.hbase.filter FilterList))
  (:import (org.apache.hadoop.hbase.util Bytes))
  (:require (clojure [pprint :as p])))

(defn- sanitize-opts
  [opts]
  (mapcat identity
          (remove #(empty? (second %))
                  (partition 2 opts))))

(defn- get*
  [table row-key & opts]
  (let [opts* (sanitize-opts opts)]
    (apply (partial hb/get table row-key) opts*)))

(defn fetch-row
  [table-name row-key & {:keys [row-as columns] :or {row-as utils/as-vector}}]
  (hb/with-table [table (hb/table table-name)]
    (row-as (get* table (Bytes/toBytes row-key) :columns columns))))

(defn- fetch-rows*
  [scanner limit batch row-as]
  (if (<= limit batch)
    (map row-as (.next scanner limit))
    (loop [rows []
           num-fetched 0]
      (let [result (.next scanner batch)]
        (if (and (seq result)
                 (not (>= num-fetched limit)))
          (recur (doall (concat rows (map row-as result)))
                 (+ num-fetched (count result)))
          (take limit rows))))))

(defn fetch-rows
  [table-name start-row end-row & {:keys [limit caching batch row-as]
                                   :or {limit 10 caching 1000 batch 100 row-as utils/as-vector}}]
  (hb/with-table [table (hb/table table-name)]
    (hb/with-scanner [scanner (hb/scan table
                                       :start-row start-row
                                       :stop-row end-row
                                       :caching caching)]
      (fetch-rows* scanner limit batch row-as))))

(defn fetch-rows-with-suffix
  [table-name suffix & {:keys [limit caching batch row-as]
                        :or {limit 10 caching 1000 batch 100 row-as utils/as-vector}}]
  (let [row-filter (f/row-filter-with-regex (format "^.*%s$" suffix))]
    (hb/with-table [table (hb/table table-name)]
      (hb/with-scanner [scanner (hb/scan table
                                         :caching caching
                                         :filter row-filter)]
        (fetch-rows* scanner limit batch row-as)))))

(comment
  (fetch-row "table" "row-key")
  (fetch-row "table" "row-key" :columns ["column-family1" ["column-qualifier1" "column-qualifier2"]])

  (fetch-rows "table-name" "start-row" "end-row" :row-as utils/result->key)
  (fetch-rows "table-name" "start-row" "end-row" :row-as utils/as-map)
  
  (fetch-rows-with-suffix "table-name" "suffix")
  (fetch-rows-with-suffix "table-name" "suffix" :row-as utils/result->key)

  (hb/with-table [table (hb/table "table")]
    (hb/get table (Bytes/toBytes "row-key"))))
