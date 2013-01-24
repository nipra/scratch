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
          (remove #(nil? (second %))
                  (partition 2 opts))))

(defn- sanitize-filters
  [filters]
  (when filters
    (if (instance? FilterList filters)
      filters
      (FilterList. filters))))

(defn- get*
  [table row-key & opts]
  (let [opts* (sanitize-opts opts)]
    (apply (partial hb/get table row-key) opts*)))

(defn- scan*
  [table & opts]
  (let [opts* (sanitize-opts opts)]
    (apply (partial hb/scan table) opts*)))

;;; Single Row
(defn fetch-row
  [table-name row-key & {:keys [row-as columns filters]
                         :or {row-as utils/as-vector}}]
  (hb/with-table [table (hb/table table-name)]
    (row-as (get* table (Bytes/toBytes row-key)
                  :columns columns
                  :filter (sanitize-filters filters)))))

;;; Batch row keys
(defn multi-get
  [table-name row-keys & {:keys [row-as columns filters]
                          :or {row-as utils/as-vector}}]
  (let [opts [:columns columns :filter filters]
        opts* (sanitize-opts opts)
        gets (map #(apply hb/get* % opts*)
                  (map #(Bytes/toBytes %) row-keys))]
    (hb/with-table [table (hb/table table-name)]
      (map row-as (.get table gets)))))

(defn fetch-row-with-column-range
  [table-name row-key min-column max-column & {:keys [row-as]
                                               :or {row-as utils/as-vector}}]
  (let [column-range-filter (f/column-range-filter min-column max-column)]
    (hb/with-table [table (hb/table table-name)]
      (row-as (get* table (Bytes/toBytes row-key)
                    :filter column-range-filter)))))

(defn- fetch-rows*
  [scanner limit batch row-as]
  (if (and limit (<= limit batch))
    (map row-as (.next scanner limit))
    (loop [rows []
           num-fetched 0]
      (let [result (.next scanner batch)]
        (if (and (seq result)
                 (if limit
                   (not (>= num-fetched limit))
                   true))
          (recur (doall (concat rows (map row-as result)))
                 (+ num-fetched (count result)))
          (if limit
            (take limit rows)
            rows))))))

;;; Generic
(defn fetch-rows+
  [table-name & {:keys [start-row stop-row filters columns limit caching batch row-as]
                 :or {limit 10
                      caching 1000
                      batch 100
                      row-as utils/as-vector}}]
  (hb/with-table [table (hb/table table-name)]
    (hb/with-scanner [scanner (scan* table
                                     :start-row start-row
                                     :stop-row stop-row
                                     :caching caching
                                     :filter (sanitize-filters filters)
                                     :columns columns)]
      (fetch-rows* scanner limit batch row-as))))

;;; Simplest one
(defn fetch-rows
  [table-name start-row stop-row & {:keys [limit caching batch row-as filters columns]
                                    :or {limit 10
                                         caching 1000
                                         batch 100
                                         row-as utils/as-vector}}]
  (fetch-rows+ table-name
               :start-row start-row
               :stop-row stop-row
               :caching caching
               :row-as row-as
               :limit limit
               :filters filters
               :columns columns))

;;; Prefix
(defn fetch-rows-with-prefix
  [table-name prefix & {:keys [limit caching batch row-as]
                        :or {limit 10
                             caching 1000
                             batch 100
                             row-as utils/as-vector}}]
  (let [prefix-filter (f/row-prefix-filter prefix)]
    (fetch-rows+ table-name
                 :filters [prefix-filter]
                 :limit limit
                 :caching caching
                 :batch batch
                 :row-as row-as)))

;;; Suffix
(defn fetch-rows-with-suffix
  [table-name suffix & {:keys [limit caching batch row-as]
                        :or {limit 10
                             caching 1000
                             batch 100
                             row-as utils/as-vector}}]
  (let [row-filter (f/row-filter-with-regex (format "^.*%s$" suffix))]
    (fetch-rows+ table-name
                 :filters [row-filter]
                 :limit limit
                 :caching caching
                 :batch batch
                 :row-as row-as)))

;;; Regex
(defn fetch-rows-with-regex
  [table-name regex-str & {:keys [limit caching batch row-as filters]
                           :or {limit 10
                                caching 1000
                                batch 100
                                row-as utils/as-vector}}]
  (let [row-filter (f/row-filter-with-regex regex-str)]
    (fetch-rows+ table-name
                 :filters (concat filters [row-filter])
                 :limit limit
                 :caching caching
                 :batch batch
                 :row-as row-as)))



(comment
  (fetch-row "table" "row-key")
  (fetch-row "table" "row-key"
             :columns ["column-family1" ["column-qualifier1" "column-qualifier2"]])
  (let [f1 (f/column-range-filter "min" "max")
        f2 (f/column-prefix-filter "pre")]
    [(fetch-row "table-name" "row-key" :filters [f1])
     (fetch-row "table-name" "row-key" :filters [f1 f2])])

  (multi-get "table-name" ["row-key1" "row-key2"] :columns ["column-family1" ["column-qualifier1"
                                                                              "column-qualifier2"]])
  
  (fetch-row-with-column-range "table-name" "row-key" "min" "max")

  (fetch-rows "table-name" "start-row" "end-row" :row-as utils/result->key)
  (fetch-rows "table-name" "start-row" "end-row" :row-as utils/as-map)
  
  (fetch-rows-with-suffix "table-name" "suffix")
  (fetch-rows-with-suffix "table-name" "suffix" :row-as utils/result->key)

  (let [f1 (f/row-filter-with-regex "ReGenX")]
    (fetch-rows-with-filters "table-name" [f1] :row-as utils/result->key))

  (let [f1 (f/row-prefix-filter "PRE-INDEPENDENCE")]
    (fetch-rows-with-filters "table-name" [f1]  :row-as utils/result->key))

  (hb/with-table [table (hb/table "table")]
    (hb/get table (Bytes/toBytes "row-key")))

  (let [column-filter (f/column-filter "column-family" "column-qualifier")]
    (fetch-rows "table-name" "start-row" "stop-row"
                :row-as utils/result->key
                :filters [column-filter]))

  (let [column-filter (f/column-filter "column-family" "column-qualifier")
        row-keys (fetch-rows "table-name" "start-row" "stop-row"
                             :row-as utils/result->key
                             :filters [column-filter]
                             :limit nil)
        result (multi-get "table-name" row-keys :columns ["column-family" ["column-qualifier" "value"]])]
    (def *result result))

  ;; Couldn't verify. Failing after retries.
  (let [column-family-filter (f/column-family-filter "column-family")
        column-qualifier-filter (f/column-qualifier-filter "column-qualifier")
        value-filter (f/value-filter "value")
        result (fetch-rows "table-name" "start-row" "stop-row"
                           :filters [column-family-filter column-qualifier-filter value-filter]
                           :limit 1)]
    (def *result2 result))

  (let [column-value-filter (f/column-value-filter "column-family" "qualifier" "value")
        result (fetch-rows "table-name" "start-row" "stop-row"
                           :filters [column-value-filter]
                           :limit 1
                           :columns ["column-family" ["qualifier"]])]
    (def *result3 result)))
