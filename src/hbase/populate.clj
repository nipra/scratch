(ns hbase.populate
  (:require (utils [date-time :as dt]
                   [seq :as seq]))
  (:require (libs.clojure-hbase [core :as hb]
                                [util :as hu] [admin :as ha]))
  (:require (hbase [utils :as u]
                   [filters :as f]
                   [query :as q]
                   [count :as c]))
  (:require (clj-time [core :as ctc]))
  (:import (org.apache.hadoop.hbase HBaseConfiguration HConstants KeyValue)
           (org.apache.hadoop.hbase.client HTable
                                           HTablePool Get Put Delete Scan Result RowLock)
           (org.apache.hadoop.hbase.util Bytes))
  (:require (clojure [pprint :as p]
                     [set :as set]))
  (:use [korma.db]
        [korma.core])
  (:require [clojure.java [jdbc :as jdbc]])
  (:require (clj-time [core :as ctc]
                      [format :as ctf]
                      [coerce :as ctco]
                      [local :as ctl]))
  (:require (utils [date-time :as dt]))
  (:require [clojure.math.combinatorics :as combo]))

(def letters ["a" "b" "c" "d" "e" "f" "g" "h" "i" "j" "k" "l" "m"
              "n" "o" "p" "q" "r" "s" "t" "u" "v" "w" "x" "y" "z"])

(def two-letters ["aa" "bb" "cc" "dd" "ee" "ff" "gg" "hh" "ii" "jj" "kk" "ll" "mm"
                  "nn" "oo" "pp" "qq" "rr" "ss" "tt" "uu" "vv" "ww" "xx" "yy" "zz"])

(def digits ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9"])

(defn get-keys
  [coll]
  (map #(apply str %) (combo/cartesian-product coll coll)))

(defn get-qualifiers
  []
  (map #(apply str %) (combo/cartesian-product digits digits)))

(defn populate
  [table-name date-string seed-key-coll & {:keys [qualifiers]}]
  (doseq [row-key (get-keys seed-key-coll)]
    (hb/with-table [table (hb/table table-name)]
      (let [dt (ctf/parse (:basic-date-time-no-ms ctf/formatters) date-string)]
        (doall
         (map #(hb/put table row-key
                       :values ["f" [%1 (str (rand-int 1000)) (ctco/to-long %2)]])
              (or qualifiers (get-qualifiers))
              (iterate #(ctc/plus % (ctc/days 1)) dt)))))))

(defn populate*
  [table-name date-string seed-key-coll & {:keys [qualifiers]}]
  (doseq [row-key (get-keys seed-key-coll)]
    (hb/with-table [table (hb/table table-name)]
      (let [dt (ctf/parse (:basic-date-time-no-ms ctf/formatters) date-string)]
        (doall
         (map #(hb/put table row-key
                       :values ["f" [%1 (rand-int 1000) (ctco/to-long %2)]])
              (or qualifiers (get-qualifiers))
              (iterate #(ctc/plus % (ctc/days 1)) dt)))))))
