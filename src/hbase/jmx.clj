(ns hbase.jmx
  (:require [clojure.java.jmx :as jmx])
  (:require [clojure [string :as s] [pprint :as p]]))

;;; Source: http://briancarper.net/blog/527/printing-a-nicely-formatted-plaintext-table-of-data-in-clojure
(defn table
  "Given a seq of hash-maps, prints a plaintext table of the values of the hash-maps.
  If passed a list of keys, displays only those keys.  Otherwise displays all the
  keys in the first hash-map in the seq."
  ([xs]
     (table xs (keys (first xs))))
  ([xs ks]
     (when (seq xs)
       (let [f (fn [old-widths x]
                 (reduce (fn [new-widths k]
                           (let [length (inc (count (str (k x))))]
                             (if (> length (k new-widths 0))
                               (assoc new-widths k length)
                               new-widths)))
                         old-widths ks))
             widths (reduce f {} (conj xs (zipmap ks ks)))
             total-width (reduce + (vals widths))
             format-string (str "~{"
                                (reduce #(str %1 "~" (%2 widths) "A") "" ks)
                                "~}~%")]
         (p/cl-format true format-string (map str ks))
         (p/cl-format true "~{~A~}~%" (repeat total-width \-))
         (doseq [x xs]
           (p/cl-format true format-string (map x ks)))))))

(def ^{:dynamic true} hbase-master "hadoop-hmaster02.sl.ss")
(def ^{:dynamic true} hbase-master-jmx-port 10101)
(def ^{:dynamic true} hbase-regionserver-jmx-port 10102)

(def regionservers-metrics [:atomicIncrementTimeAvgTime
                            :atomicIncrementTimeMaxTime
                            :atomicIncrementTimeMinTime
                            :atomicIncrementTimeNumOps
                            :blockCacheCount
                            :blockCacheEvictedCount
                            :blockCacheFree
                            :blockCacheHitCachingRatio
                            :blockCacheHitCachingRatioPastNPeriods
                            :blockCacheHitCount
                            :blockCacheHitRatio
                            :blockCacheHitRatioPastNPeriods
                            :blockCacheMissCount
                            :blockCacheSize
                            :checksumFailuresCount
                            :compactionQueueSize
                            :compactionSizeAvgTime
                            :compactionSizeMaxTime
                            :compactionSizeMinTime
                            :compactionSizeNumOps
                            :compactionTimeAvgTime
                            :compactionTimeMaxTime
                            :compactionTimeMinTime
                            :compactionTimeNumOps
                            :flushQueueSize
                            :flushSizeAvgTime
                            :flushSizeMaxTime
                            :flushSizeMinTime
                            :flushSizeNumOps
                            :flushTimeAvgTime
                            :flushTimeMaxTime
                            :flushTimeMinTime
                            :flushTimeNumOps
                            :fsPreadLatencyAvgTime
                            :fsPreadLatencyHistogram_75th_percentile
                            :fsPreadLatencyHistogram_95th_percentile
                            :fsPreadLatencyHistogram_99th_percentile
                            :fsPreadLatencyHistogram_max
                            :fsPreadLatencyHistogram_mean
                            :fsPreadLatencyHistogram_median
                            :fsPreadLatencyHistogram_min
                            :fsPreadLatencyHistogram_num_ops
                            :fsPreadLatencyHistogram_std_dev
                            :fsPreadLatencyMaxTime
                            :fsPreadLatencyMinTime
                            :fsPreadLatencyNumOps
                            :fsReadLatencyAvgTime
                            :fsReadLatencyHistogram_75th_percentile
                            :fsReadLatencyHistogram_95th_percentile
                            :fsReadLatencyHistogram_99th_percentile
                            :fsReadLatencyHistogram_max
                            :fsReadLatencyHistogram_mean
                            :fsReadLatencyHistogram_median
                            :fsReadLatencyHistogram_min
                            :fsReadLatencyHistogram_num_ops
                            :fsReadLatencyHistogram_std_dev
                            :fsReadLatencyMaxTime
                            :fsReadLatencyMinTime
                            :fsReadLatencyNumOps
                            :fsSyncLatencyAvgTime
                            :fsSyncLatencyMaxTime
                            :fsSyncLatencyMinTime
                            :fsSyncLatencyNumOps
                            :fsWriteLatencyAvgTime
                            :fsWriteLatencyHistogram_75th_percentile
                            :fsWriteLatencyHistogram_95th_percentile
                            :fsWriteLatencyHistogram_99th_percentile
                            :fsWriteLatencyHistogram_max
                            :fsWriteLatencyHistogram_mean
                            :fsWriteLatencyHistogram_median
                            :fsWriteLatencyHistogram_min
                            :fsWriteLatencyHistogram_num_ops
                            :fsWriteLatencyHistogram_std_dev
                            :fsWriteLatencyMaxTime
                            :fsWriteLatencyMinTime
                            :fsWriteLatencyNumOps
                            :fsWriteSizeAvgTime
                            :fsWriteSizeMaxTime
                            :fsWriteSizeMinTime
                            :fsWriteSizeNumOps
                            :hdfsBlocksLocalityIndex
                            :mbInMemoryWithoutWAL
                            :memstoreSizeMB
                            :numPutsWithoutWAL
                            :readRequestsCount
                            :regionSplitFailureCount
                            :regionSplitSuccessCount
                            :regions
                            :requests
                            :rootIndexSizeKB
                            :slowHLogAppendCount
                            :slowHLogAppendTimeAvgTime
                            :slowHLogAppendTimeMaxTime
                            :slowHLogAppendTimeMinTime
                            :slowHLogAppendTimeNumOps
                            :storefileIndexSizeMB
                            :storefiles
                            :stores
                            :totalStaticBloomSizeKB
                            :totalStaticIndexSizeKB
                            :updatesBlockedSecondsHighWater_75th_percentile
                            :updatesBlockedSecondsHighWater_95th_percentile
                            :updatesBlockedSecondsHighWater_99th_percentile
                            :updatesBlockedSecondsHighWater_max
                            :updatesBlockedSecondsHighWater_mean
                            :updatesBlockedSecondsHighWater_median
                            :updatesBlockedSecondsHighWater_min
                            :updatesBlockedSecondsHighWater_num_ops
                            :updatesBlockedSecondsHighWater_std_dev
                            :updatesBlockedSeconds_75th_percentile
                            :updatesBlockedSeconds_95th_percentile
                            :updatesBlockedSeconds_99th_percentile
                            :updatesBlockedSeconds_max
                            :updatesBlockedSeconds_mean
                            :updatesBlockedSeconds_median
                            :updatesBlockedSeconds_min
                            :updatesBlockedSeconds_num_ops
                            :updatesBlockedSeconds_std_dev
                            :writeRequestsCount])

(defn get-regionservers
  []
  (jmx/with-connection {:host hbase-master :port hbase-master-jmx-port}
    (let [regionservers (keys (:RegionServers (jmx/mbean "hadoop:service=Master,name=Master")))]
      (map #(-> % name (s/split #",") first) regionservers))))

(defn get-regionserver-metrics*
  [regionserver metrics]
  
  (jmx/with-connection {:host regionserver :port hbase-regionserver-jmx-port}
    (metrics (jmx/mbean "hadoop:service=RegionServer,name=RegionServerStatistics"))))

(defn get-regionserver-metrics
  [metrics]
  (let [regionservers (get-regionservers)
        metrics* (map #(reduce (fn [x y]
                                 (merge x {y (get-regionserver-metrics* % y)}))
                               {}
                               metrics)
                      regionservers)
        metrics** (map (fn [x y] (merge {:host x} y)) regionservers metrics*)]
    (sort-by :host metrics**)))

(defn block-locality-index
  ([]
     (get-regionserver-metrics :hdfsBlocksLocalityIndex))
  ([regionserver]
     (get-regionserver-metrics regionserver :hdfsBlocksLocalityIndex)))

(defn massager
  [hash-map field fun]
  (update-in hash-map [field] fun))

(defn million
  [x]
  (str (float (/ x 1000 1000)) " mn"))

(defn mb
  [x]
  (str (float (/ x 1024 1024)) " MB"))

(defn thousand
  [x]
  (str (float (/ x 1000)) " K"))

;;; Usage
(comment
  (p/pprint (get-regionserver-metrics [:hdfsBlocksLocalityIndex]))
  (p/pprint (get-regionserver-metrics [:stores :storefiles]))

  (p/pprint (get-regionserver-metrics [:blockCacheHitRatio :blockCacheHitCount :blockCacheMissCount]))
  (table (get-regionserver-metrics [:blockCacheHitRatio :blockCacheHitCount :blockCacheMissCount])
         [:host :blockCacheHitRatio :blockCacheHitCount :blockCacheMissCount])
  (p/print-table [:host :blockCacheHitRatio :blockCacheHitCount :blockCacheMissCount]
                 (get-regionserver-metrics [:blockCacheHitRatio
                                            :blockCacheHitCount
                                            :blockCacheMissCount]))

  (p/pprint (get-regionserver-metrics [:compactionTimeAvgTime]))

  (p/pprint (get-regionserver-metrics [:flushQueueSize]))

  (p/pprint (get-regionserver-metrics [:blockCacheSize]))
  (p/pprint (get-regionserver-metrics [:blockCacheCount]))
  (p/pprint (get-regionserver-metrics [:blockCacheEvictedCount :blockCacheCount])))


(comment
  (binding [hbase-master "localhost"]
    (get-regionservers)))

(comment
  (jmx/with-connection {:host "localhost" :port 3000}
    (jmx/mbean "java.lang:type=Memory"))

  (jmx/attribute-names "java.lang:type=Memory")

  (jmx/mbean-names "*:*"))

;;; 10101 => Master
;;; 10102 => Regionserver
(comment
  (jmx/with-connection {:host "localhost" :port 10101}
    (jmx/mbean "java.lang:type=Memory"))

  (jmx/with-connection {:host "localhost" :port 10101}
    (seq (jmx/mbean-names "*:*")))

  (jmx/with-connection {:host "localhost" :port 10101}
    ;; Large value
    (jmx/mbean "hadoop:service=Master,name=Master"))

  (jmx/with-connection {:host "localhost" :port 10101}
    (keys (jmx/mbean "hadoop:service=Master,name=Master")))

  (jmx/with-connection {:host "localhost" :port 10101}
    (def *rs (:RegionServers (jmx/mbean "hadoop:service=Master,name=Master"))))

  (jmx/with-connection {:host "localhost" :port 10101}
    (jmx/mbean "hadoop:service=Master,name=MasterStatistics")))

(comment
  (jmx/with-connection {:host "localhost" :port 10102}
    (jmx/mbean "java.lang:type=Memory"))

  (jmx/with-connection {:host "localhost" :port 10102}
    (seq (jmx/mbean-names "*:*")))

  (jmx/with-connection {:host "localhost" :port 10102}
    (jmx/mbean "hadoop:service=RegionServer,name=RegionServer"))

  (jmx/with-connection {:host "localhost" :port 10102}
    (jmx/mbean "hadoop:service=RegionServer,name=RegionServerStatistics")))
