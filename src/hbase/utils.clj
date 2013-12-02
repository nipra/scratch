(ns hbase.utils
  (:gen-class)
  (:require [libs.clojure-hbase [core :as hb] [util :as hu] [admin :as ha]])
  ;; (:require [libs.com.compass.hbase.client :as c])
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

(def ^{:dynamic true} *conf* nil)

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

(defn as-vector-local
  [result]
  (map #(cons (result->key result) %)
       (hb/as-vector result
                     :map-value hu/as-str
                     :map-family hu/as-str
                     :map-qualifier hu/as-str
                     :map-timestamp (comp ctl/to-local-date-time ctco/from-long))))

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

(defn sanitize-opts
  [opts]
  (mapcat identity
          (remove #(nil? (second %))
                  (partition 2 opts))))

(defn scan*
  [table & opts]
  (let [opts* (sanitize-opts opts)]
    (apply (partial hb/scan table) opts*)))

;;;
(defn htable
  [table-name]
  (HTable. *conf* (Bytes/toBytes table-name)))

(defmacro with-htable
  [bindings & body]
  {:pre [(vector? bindings)
         (even? (count bindings))]}
  (cond
    (= (count bindings) 0) `(do ~@body)
    (symbol? (bindings 0)) `(let ~(subvec bindings 0 2)
                              (try
                                (with-htable ~(subvec bindings 2) ~@body)
                                (finally
                                  (.close ~(bindings 0)))))
    :else (throw (IllegalArgumentException.
                  "Bindings must be symbols."))))

(defn pair->vec
  [pair]
  (map #(vector (hu/as-str %) (hu/as-str %2))
       (.getFirst pair) (.getSecond pair)))

(defn get-start-end-keys
  [table-name]
  (with-htable [table (htable table-name)]
    (pair->vec (.getStartEndKeys table))))

(defn row-key->del
  [row-key]
  (Delete. (Bytes/toBytes row-key)))

(defn row-keys->dels
  [row-keys]
  (map row-key->del row-keys))

(defn del-row
  [table-name row-key]
  (hb/with-table [table (hb/table table-name)]
    (.delete table (row-key->del row-key))))

(defn del-rows
  [table-name row-keys]
  (hb/with-table [table (hb/table table-name)]
    (.delete table (row-keys->dels row-keys))))

(defn cell->row-key
  [cell]
  (first cell))

;;; 
(defn cell->column-family
  [cell]
  (second cell))

(defn cell->column-qualifier
  [cell]
  (nth cell 2))

(defn cell->date-time
  [cell]
  (nth cell 3))

(defn cell->timestamp
  [cell]
  (ctco/to-long (cell->date-time cell)))

(defn cell->value
  [cell]
  (nth cell 4))

(defn result->cells
  [result]
  (apply concat result))

(comment
  (with-htable [table (htable "table-name")]
    (doall (pair->vec (.getStartEndKeys table)))))
