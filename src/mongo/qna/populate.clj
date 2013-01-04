(ns mongo.qna.populate
  (:use [monger.core :only [connect! connect set-db! get-db connect set-default-write-concern!]]
        [monger.collection :only [insert insert-batch update]])
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
  (:require (clojure [pprint :as p]
                     set)))

(comment
  (do
    (connect!)
    (set-db! (monger/get-db "qna"))))

(defonce user-ids (atom []))
(defonce question-ids (atom []))
(defonce answer-ids (atom []))

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
            followers (distinct (conj (take (rand-int 100)
                                            (repeatedly 1000 #(rand-nth user-ids)))
                                      asked-by))]
        (insert "questions" {:_id id
                             :title (generate-random-question)
                             :description (first (fl/paragraphs 5))
                             :created-at (ctc/minus (ctc/now) (ctc/days (rand-int 1000)))
                             :asked-by asked-by
                             :followers followers})
        (swap! question-ids conj id)))))

(defn populate-answers
  []
  (let [user-ids @user-ids
        question-ids @question-ids]
    (alter-var-root (var answer-ids) (constantly (atom [])))
    (doseq [question-id question-ids]
      (let [user-ids (take (rand-int 10) (distinct (repeatedly 10 #(rand-nth user-ids))))]
        (doseq [user-id user-ids]
          (let [id (ObjectId.)
                asked-at (:created-at (coll/find-map-by-id "questions" question-id [:created-at]))
                created-at (ctc/plus asked-at (ctc/days (rand-int 30)))
                random-voters (fn [n user-id]
                                (remove (partial = user-id)
                                        (take (rand-int n)
                                              (distinct (repeatedly 1000 #(rand-nth user-ids))))))
                upvoters (random-voters 500 user-id)
                downvoters (clojure.set/difference (set (random-voters 50 user-id))
                                                   (set upvoters))
                votes (- (count upvoters) (count downvoters))
                answer {:_id id
                        :text (first (fl/paragraphs 30))
                        :answered-by user-id
                        :question-id question-id
                        :upvoters upvoters
                        :downvoters downvoters
                        :votes votes
                        :created-at created-at
                        :comments []}]
            (insert "answers" answer)
            (swap! answer-ids conj id)))))))

(defn populate-comments
  []
  (let [user-ids @user-ids
        answer-ids @answer-ids]
    (doseq [answer-id answer-ids]
      (let [user-ids (take (rand-int 10) (distinct (repeatedly 10 #(rand-nth user-ids))))]
        (doseq [user-id user-ids]
          (let [id (ObjectId.)
                answered-at (:created-at (coll/find-map-by-id "answers" answer-id [:created-at]))
                created-at (ctc/plus answered-at (ctc/minutes (rand-int 720)))
                comment {:_id id
                         :text (first (fl/paragraphs))
                         :commented-by user-id
                         :answer-id answer-id}]
            (insert "comments" comment)
            (update "answers" {:_id answer-id} {"$push" {:comments id}})))))))

(comment
  (coll/find-one-as-map "users" {:age {"$gte" 20}})
  (p/pprint (query/with-collection "answers"
              (query/find {:downvoters {"$ne" []}})
              (query/limit 2)
              (query/skip 4)))
  (coll/count "answers" {:downvoters {"$ne" []}})
  (coll/count "answers" {:votes {"$gte" 100 }})
  (p/pprint (query/with-collection "answers"
              (query/find {})
              (query/sort {:upvoters -1})
              (query/limit 1))))

(comment
  (let [n 10000]
    (coll/drop "users")
    (coll/drop "questions")
    (coll/drop "answers")
    (coll/drop "comments")
    (populate-users :n n)
    (populate-questions :n n)
    (populate-answers)
    (populate-comments))
  (do
    (def user-ids (atom []))
    (def question-ids (atom []))
    (def answer-ids (atom []))))
