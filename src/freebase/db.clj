(ns freebase.db
  (:require [utils.postgres :as pg])
  (:use [korma.db]
        [korma.core])
  (:require [clojure.java [jdbc :as sql]]
            [clojure.pprint :as p])
  (:import [java.sql Connection DriverManager ResultSet SQLException
            Statement ResultSetMetaData]))

(def freebase-wex {:classname "org.postgresql.Driver"
                   :subprotocol "postgresql"
                   :subname "//localhost/wikipedia"
                   :user "postgres"
                   :password "postgres"})

(defdb wex (postgres {:db "wikipedia"
                      :user "postgres"
                      :password "postgres"}))



(defentity articles)

(comment
  (select (database articles wex)
          (limit 1)))
