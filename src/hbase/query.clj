(ns hbase.query
  (:require (libs.clojure-hbase [core :as hb]
                                [util :as hu]
                                [admin :as ha]))
  (:require (hbase [utils :as u] [filters :as f]))
  (:import (org.apache.hadoop.hbase.filter FilterList))
  (:import (org.apache.hadoop.hbase.util Bytes))
  (:require (clojure [pprint :as p])))

(defn- get*
  [table row-key & opts]
  (let [opts* (u/sanitize-opts opts)]
    (apply (partial hb/get table row-key) opts*)))

;;; Single Row
(defn fetch-row*
  [table-name row-key & {:keys [row-as columns filters key-only?]
                         :or {row-as (if key-only? u/result->key u/as-vector)}}]
  (hb/with-table [table (hb/table table-name)]
    (row-as (get* table (Bytes/toBytes row-key)
                  :columns columns
                  :filter (f/sanitize-filters filters key-only?)))))

;;; Batch row keys
(defn multi-get
  [table-name row-keys & {:keys [row-as columns filters key-only?]
                          :or {row-as (if key-only? u/result->key u/as-vector)}}]
  (let [opts [:columns columns :filter (f/sanitize-filters filters key-only?)]
        opts* (u/sanitize-opts opts)
        gets (map #(apply hb/get* % opts*)
                  (map #(Bytes/toBytes %) row-keys))]
    (hb/with-table [table (hb/table table-name)]
      (map row-as (.get table gets)))))

(defn fetch-row-with-column-range
  [table-name row-key min-column max-column & {:keys [row-as]
                                               :or {row-as u/as-vector}}]
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
  [table-name & {:keys [start-row stop-row filters columns limit caching batch row-as key-only?]
                 :or {limit 10
                      caching 1000
                      batch 100
                      row-as (if key-only? u/result->key u/as-vector)}}]
  (hb/with-table [table (hb/table table-name)]
    (hb/with-scanner [scanner (u/scan* table
                                       :start-row start-row
                                       :stop-row stop-row
                                       :caching caching
                                       :filter (f/sanitize-filters filters key-only?)
                                       :columns columns)]
      (fetch-rows* scanner limit batch row-as))))

;;; Simplest one
(defn fetch-rows
  [table-name start-row stop-row & {:keys [limit caching batch row-as filters columns key-only?]
                                    :or {limit 10
                                         caching 1000
                                         batch 100
                                         row-as (if key-only? u/result->key u/as-vector)}}]
  (fetch-rows+ table-name
               :start-row start-row
               :stop-row stop-row
               :caching caching
               :row-as row-as
               :limit limit
               :filters filters
               :columns columns
               :key-only? key-only?))

;;; Prefix
(defn fetch-rows-with-prefix
  [table-name prefix & {:keys [limit caching batch row-as key-only?]
                        :or {limit 10
                             caching 1000
                             batch 100
                             row-as (if key-only? u/result->key u/as-vector)}}]
  (let [prefix-filter (f/row-prefix-filter prefix)]
    (fetch-rows+ table-name
                 :filters [prefix-filter]
                 :limit limit
                 :caching caching
                 :batch batch
                 :row-as row-as
                 :key-only? key-only?)))

;;; Suffix
(defn fetch-rows-with-suffix
  [table-name suffix & {:keys [limit caching batch row-as key-only?]
                        :or {limit 10
                             caching 1000
                             batch 100
                             row-as (if key-only? u/result->key u/as-vector)}}]
  (let [row-filter (f/row-filter-with-regex (format "^.*%s$" suffix))]
    (fetch-rows+ table-name
                 :filters [row-filter]
                 :limit limit
                 :caching caching
                 :batch batch
                 :row-as row-as
                 :key-only? key-only?)))

;;; Regex
(defn fetch-rows-with-regex
  [table-name regex-str & {:keys [limit caching batch row-as filters key-only?]
                           :or {limit 10
                                caching 1000
                                batch 100
                                row-as (if key-only? u/result->key u/as-vector)}}]
  (let [row-filter (f/row-filter-with-regex regex-str)]
    (fetch-rows+ table-name
                 :filters (concat filters [row-filter])
                 :limit limit
                 :caching caching
                 :batch batch
                 :row-as row-as
                 :key-only? key-only?)))

;;; Random
(defn random-rows
  [table-name start-row stop-row & {:keys [limit caching batch row-as filters columns key-only? n]
                                    :or {limit 10
                                         caching 1000
                                         batch 100
                                         row-as (if key-only? u/result->key u/as-vector)
                                         n (rand)}}]
  (fetch-rows+ table-name
               :start-row start-row
               :stop-row stop-row
               :caching caching
               :row-as row-as
               :limit limit
               :filters (conj filters (f/random-row-filter n))
               :columns columns
               :key-only? key-only?))

(defn get-nth-row-key
  "`n' must be >= to `caching'"
  [table-name start-row stop-row n & {:keys [caching filters]
                                      :or {caching 1000}}]
  (try
    (hb/with-table [table (hb/table table-name)]
      (hb/with-scanner [scanner (u/scan* table
                                         :start-row start-row
                                         :stop-row stop-row
                                         :caching caching
                                         :filter (f/sanitize-filters filters true))]
        (loop [result (.next scanner caching)
               num-keys (count result)
               m num-keys]
          (if (seq result)
            (cond
              (= m n)
              (u/result->key (last result))

              (> m n)
              (u/result->key (nth result (dec (mod n caching))))

              :else
              (let [result2 (.next scanner caching)
                    num-keys2 (count result2)
                    m2 (+ m num-keys2)]
                (recur result2 num-keys2 m2)))

            (u/result->key (last result))))))
    (catch NullPointerException _ nil)))

(defn get-rows-with-pagination
  [table-name start-row stop-row offset limit & {:keys [caching filters row-as key-only?]
                                                 :or {caching 1000
                                                      row-as (if key-only? u/result->key u/as-vector)}}]
  (when-let [row-offset (get-nth-row-key table-name start-row stop-row offset
                                         :caching caching
                                         :filters filters)]
    (fetch-rows table-name row-offset stop-row
                :limit limit
                :caching caching
                :batch caching
                :filters filters
                :row-as row-as
                :key-only? key-only?)))

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

  (fetch-rows "table-name" "start-row" "end-row" :row-as u/result->key)
  (fetch-rows "table-name" "start-row" "end-row" :row-as u/as-map)
  
  (fetch-rows-with-suffix "table-name" "suffix")
  (fetch-rows-with-suffix "table-name" "suffix" :row-as u/result->key)

  (let [f1 (f/row-filter-with-regex "ReGenX")]
    (fetch-rows-with-filters "table-name" [f1] :row-as u/result->key))

  (let [f1 (f/row-prefix-filter "PRE-INDEPENDENCE")]
    (fetch-rows-with-filters "table-name" [f1]  :row-as u/result->key))

  (hb/with-table [table (hb/table "table")]
    (hb/get table (Bytes/toBytes "row-key")))

  (let [column-filter (f/column-filter "column-family" "column-qualifier")]
    (fetch-rows "table-name" "start-row" "stop-row"
                :row-as u/result->key
                :filters [column-filter]))

  (let [column-filter (f/column-filter "column-family" "column-qualifier")
        row-keys (fetch-rows "table-name" "start-row" "stop-row"
                             :row-as u/result->key
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
    (def *result3 result))

  (count-rows "table-name" "start-row" "stop-row"
              :filters [(f/row-filter-with-regex ".+(word1|word2|word3)$")])
  (fetch-rows "table-name" "start-row" "stop-row"
              :limit nil
              :key-only? true
              :filters [(f/row-filter-with-regex ".+(word1|word2|word3)$")])
  (count-rows "table-name" nil nil ; Don't specify start-row and stop row
              :filters [(f/row-filter-with-regex ".+keyword$")])
  (count-rows "table-name" "start-row" "stop-row"
              :limit nil
              :filters [(f/column-range-filter "column1" "column2")])
  (multi-get "table-name" ["row1" "row2"]
             :columns ["column-family" (dt/yyyymmdd-range "start-date" "stop-date") ]))
