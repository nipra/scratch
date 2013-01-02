(ns mongo.scratch
  (:use [monger.core :only [connect! connect set-db! get-db connect set-default-write-concern!]]
        [monger.collection :only [insert insert-batch]])
  (:require (monger [core :as monger]
                    [collection :as coll]))
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(comment
  (def *client (connect))
  (monger.core/get-db-names *client)
  (def *nipra (monger.core/get-db *client "nipra"))
  (insert *nipra "nipra" {"a" 1} WriteConcern/SAFE))

(comment
  (require 'mongo.init)
  (def *nipra (monger.core/get-db "nipra"))
  (insert *nipra "nipra" {"a" 1} WriteConcern/SAFE))

(comment
  (require 'mongo.init)
  (monger/with-db (monger.core/get-db "nipra")
    (insert "nipra" {"a" 1} WriteConcern/SAFE)))

(comment
  (require 'mongo.init)
  (set-db! (monger/get-db "nipra"))
  (insert "nipra" {"a" 1}))
