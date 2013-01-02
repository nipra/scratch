(ns mongo.init
  (:use [monger.core :only [connect! connect set-db! get-db connect set-default-write-concern!]]
        [monger.collection :only [insert insert-batch]])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(connect!)
