(ns mongo.populate.qna
  (:use [monger.core :only [connect! connect set-db! get-db connect set-default-write-concern!]]
        [monger.collection :only [insert insert-batch]])
  (:require (monger [core :as monger]
                    [collection :as coll]
                    [joda-time :as joda]
                    [query :as query]))
  (:require (faker [company :as fc]
                   [company-data :as fcd]
                   [internet :as fi]
                   [lorem :as fl]
                   [lorem-data :as fld]
                   [address :as fa]
                   [address-data :as fad]
                   [name :as fn]
                   [name-data :as fnd]
                   [phone-number :as fp]))
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern])
  (:require (clj-time [core :as ctc]))
  (:require [clojure.pprint :as p]))

(comment
  (do
    (connect!)
    (set-db! (monger/get-db "qna"))))

(defonce user-ids (atom []))
(defonce question-ids (atom []))

(defn populate-users
  [& {:keys [n] :or {n 1000}}]
  (alter-var-root (var user-ids) (constantly (atom [])))
  (dotimes [_ n]
    (let [id (ObjectId.)]
      (insert "users" {:_id id
                       :name (first (fn/names))
                       :phone (first (fp/phone-numbers))
                       :address (fa/street-address)
                       :organisation (first (fc/names))
                       :bio (first (fl/paragraphs))
                       :created-at (ctc/minus (ctc/now) (ctc/days (rand-int 1000)))
                       :age (rand-nth (range 18 75))})
      (swap! user-ids conj id))))

(defn generate-random-question
  []
  (clojure.string/replace (first (fl/sentences 10)) #"[.]$" "?"))

(defn populate-questions
  [& {:keys [n] :or {n 1000}}]
  (let [user-ids @user-ids]
    (alter-var-root (var question-ids) (constantly (atom [])))
    (dotimes [_ n]
      (let [id (ObjectId.)
            asked-by (rand-nth user-ids)
            followed-by (distinct (conj (take (rand-int 100)
                                              (repeatedly 1000 #(rand-nth user-ids)))
                                        asked-by))]
        (insert "questions" {:_id id
                             :title (generate-random-question)
                             :description (first (fl/paragraphs 5))
                             :created-at (ctc/minus (ctc/now) (ctc/days (rand-int 1000)))
                             :asked-by asked-by
                             :followed-by followed-by})
        (swap! question-ids conj id)))))

(defn- random-voters
  [user-id]
  (remove (partial = user-id)
          (take (rand-int 500)
                (distinct (repeatedly 1000 #(rand-nth user-ids))))))

(defn populate-answers
  []
  (let [user-ids @user-ids
        question-ids @question-ids]
    (doseq [question-id question-ids]
      (let [user-ids (take (rand-int 10) (distinct (repeatedly 10 #(rand-nth user-ids))))]
        (doseq [user-id user-ids]
          (let [id (ObjectId.)
                asked-at (:created-at (coll/find-map-by-id "questions" question-id [:created-at]))
                created-at (ctc/plus asked-at (ctc/days (rand-int 30)))
                random-voters (fn [user-id]
                                (remove (partial = user-id)
                                        (take (rand-int 500)
                                              (distinct (repeatedly 1000 #(rand-nth user-ids))))))
                upvoters (random-voters user-id)
                downvoters (clojure.set/difference (set (random-voters user-id))
                                                   (set upvoters))
                votes (- (count upvoters) (count downvoters))
                answer {:_id id
                        :text (first (fl/paragraphs 30))
                        :answered-by user-id
                        :question-id question-id
                        :upvoters upvoters
                        :downvoters downvoters
                        :votes votes
                        :created-at created-at}]
            (insert "answers" answer)))))))

(comment
  (coll/find-one-as-map "users" {:age {"$gte" 20}})
  (p/pprint (query/with-collection "answers"
              (query/find {:downvoters {"$ne" []}})
              (query/limit 2)
              (query/skip 4)))
  (coll/count "answers" {:downvoters {"$ne" []}}))

(comment
  (let [n 10]
    (coll/drop "users")
    (coll/drop "questions")
    (coll/drop "answers")
    (populate-users :n n)
    (populate-questions :n n)
    (populate-answers :n n))
  (do
    (def user-ids (atom []))
    (def question-ids (atom []))))
