(ns hbase.query
  (:require (libs.clojure-hbase [core :as hb]
                                [util :as hu]
                                [admin :as ha]))
  (:require (hbase [utils :as utils] )))

(defn fetch-rows
  [table-name start-row end-row & {:keys [caching batch row-as] :or {caching 1000 batch 100 row-as utils/as-vector}}]
  (hb/with-table [table (hb/table table-name)]
    (hb/with-scanner [scanner (hb/scan table
                                       :start-row start-row
                                       :stop-row end-row
                                       :caching caching)]
      (loop [final-results []]
        (let [results (.next scanner batch)]
          (if (seq results)
            (recur (doall (concat final-results results)))
            (map row-as final-results)))))))
