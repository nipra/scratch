(ns mongo.populate
  (:use [monger.core :only [connect! connect set-db! get-db connect set-default-write-concern!]]
        [monger.collection :only [insert insert-batch]])
  (:require (monger [core :as monger]
                    [collection :as coll]
                    [joda-time]))
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
  (:require (clj-time [core :as ctc])))

(comment
  (do
    (connect!)
    (set-db! (monger/get-db "nipra"))))

(defn populate-users
  [& {:keys [n] :or {n 1000}}]
  (dotimes [_ n]
    (insert "users" {:name (first (fn/names))
                     :phone (first (fp/phone-numbers))
                     :address (fa/street-address)
                     :organisation (first (fc/names))
                     :bio (first (fl/paragraphs))
                     :created-at (ctc/minus (ctc/now) (ctc/days (rand-int 1000)))
                     :age (rand-nth (range 18 75))})))
