(ns hbase.utils
  (:gen-class)
  (:require [libs.clojure-hbase [core :as hb] [util :as hu] [admin :as ha]])
  (:require [libs.com.compass.hbase.client :as c])
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


(defn get-configuration
  [table-name]
  (let [conf (.getConfiguration (hb/table table-name))
        conf-iterator (.iterator conf)]
    (loop [entry (try
                   (.next conf-iterator)
                   (catch java.util.NoSuchElementException _ nil))
           result {}]
      (if entry
        (recur (try
                 (.next conf-iterator)
                 (catch java.util.NoSuchElementException _ nil))
               (assoc result (.getKey entry) (.getValue entry)))
        result))))

(defn get-configuration*
  [table-name]
  (let [conf (.getConfiguration (hb/table table-name))]
    (into {} conf)))

(defn get-configuration-obj
  [table-name]
  (.getConfiguration (hb/table table-name)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Stats/Status
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn server-load
  [cluster-status server & [regions-load?]]
  (let [load (.getLoad cluster-status server)
        server-load {:coprocessors (vec (.getCoprocessors load))
                     :load (.getLoad load)
                     :max-heap-MB (.getMaxHeapMB load)
                     :mem-store-size-in-MB (.getMemStoreSizeInMB load)
                     :number-of-regions (.getNumberOfRegions load)
                     :number-of-requests (.getNumberOfRequests load)
                     :storefile-index-size-in-MB (.getStorefileIndexSizeInMB load)
                     :storefiles (.getStorefiles load)
                     :storefile-size-in-MB (.getStorefileSizeInMB load)
                     :total-number-of-requests (.getTotalNumberOfRequests load)
                     :used-heap-MB (.getUsedHeapMB load)
                     :version (.getVersion load)
                     :summary (str load)
                     :server-info (str server)}]
    
    (if regions-load?
      (merge server-load {:regions-load (.getRegionsLoad load)})
      server-load)))

;;; (clojure.pprint/pprint (cluster-status :load-details? false))
(defn cluster-status
  [& {:keys [regions-load? load-details?] :or {load-details? true regions-load? false}}]
  (let [status (ha/cluster-status)
        servers (vec (.getServers status))
        load-details (when load-details? {:load-details (map #(server-load status % regions-load?) servers)})]
    (merge load-details
           {:average-load (.getAverageLoad status)
            :backup-masters (vec (.getBackupMasters status))
            :backup-masters-size (.getBackupMastersSize status)
            :cluster-id (.getClusterId status)
            :dead-server-names (vec (.getDeadServerNames status))
            :dead-servers (.getDeadServers status)
            :hbase-version (.getHBaseVersion status)
            :servers servers
            :master (str (.getMaster status))
            :master-coprocessors (vec (.getMasterCoprocessors status))
            :regions-count (.getRegionsCount status)
            :regions-in-transition (into {} (.getRegionsInTransition status))
            :requests-count (.getRequestsCount status)
            :servers-size (.getServersSize status)
            :version (.getVersion status)})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Filters
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn row-filter-with-regex
  [regex]
  (RowFilter. CompareFilter$CompareOp/EQUAL (RegexStringComparator. regex)))


(defn column-filter
  [column-family qualifier]
  (let [qualifier-filter (QualifierFilter. CompareFilter$CompareOp/EQUAL
                                           (BinaryComparator. (Bytes/toBytes qualifier)))
        family-filter (FamilyFilter. CompareFilter$CompareOp/EQUAL
                                     (BinaryComparator. (Bytes/toBytes column-family)))]
    (FilterList. [qualifier-filter family-filter])))


(defn qualifier-filter
  [qualifier]
  (QualifierFilter. CompareFilter$CompareOp/EQUAL (BinaryComparator. (Bytes/toBytes qualifier))))


(defn family-filter
  [family]
  (FamilyFilter. CompareFilter$CompareOp/EQUAL (BinaryComparator. (Bytes/toBytes family))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Count
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
  (let [filters [(qualifier-filter qualifier) (family-filter column-family)]]
    (count-rows-with-filters table-name filters)))


(defn count-rows-with-suffix
  "WARNING: Uses RegexStringComparator, so be sure to escape characters with special meaning in a regex."
  [table-name suffix]
  (let [row-filter (row-filter-with-regex (format "^.*%s$" suffix))]
    (count-rows-with-filters table-name [row-filter])))
