(ns mongo.qna.app
  (:use [monger.core :only [connect! connect set-db! get-db connect set-default-write-concern!]]
        [monger.collection :only [insert insert-batch update]])
  (:require (monger [core :as monger]
                    [collection :as coll]
                    [joda-time :as joda]
                    [query :as query]))
  (:use (mongo.qna [utils :only [with-qna]]))
  (:require (mongo.qna [utils :as mqu]))
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern])
  (:require (clj-time [core :as ctc]))
  (:require [clojure.pprint :as p]))

(defn massage-answer
  [answer]
  (let [comments (coll/find-maps "comments" {:answer-id (:_id answer)})]
    (assoc answer :comments comments)))

(defn fetch-question-details
  [question-id]
  (with-qna
    (let [question (coll/find-map-by-id "questions" question-id)
          answers (map massage-answer
                       (coll/find-maps "answers" {:question-id question-id}))]
      (assoc question :answers answers))))

(comment
  (with-qna
    (query/with-collection "questions"
      (query/find {})
      (query/fields [:_id])
      (query/skip 100)
      (query/limit 2))))
