(ns mongo.qna.utils
  (:require (monger [core :as monger]
                    [collection :as coll]
                    [joda-time :as joda]
                    [query :as query]))
  (:import [org.bson.types ObjectId])
  (:require [clojure.pprint :as p]))

(defmacro with-qna
  [& body]
  `(monger/with-db (monger/get-db "qna")
     ~@body))

(defn get-question-ids
  [skip limit]
  (map :_id
       (with-qna
         (query/with-collection "questions"
           (query/find {})
           (query/fields [:_id])
           (query/skip skip)
           (query/limit limit)))))
