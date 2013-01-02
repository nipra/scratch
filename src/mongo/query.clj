(ns mongo.query
  (:require (monger [core :as monger]
                    [collection :as coll]
                    [joda-time :as joda]
                    [query :as query])))

;;; Skip operation can be very slow for large `n'
(defn find-random-doc
  ([coll]
     (let [n (coll/count coll)]
       (query/with-collection coll
         (query/find {})
         (query/skip (rand-int n))
         (query/limit 1))))
  ([db coll]
     (monger/with-db db (find-random-doc coll))))
