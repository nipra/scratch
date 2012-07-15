(ns utils.postgres
  (:use [korma.db]
        [korma.core])
  (:require [clojure.java [jdbc :as sql]]
            [clojure.pprint :as p])
  (:import [java.sql Connection DriverManager ResultSet SQLException
            Statement ResultSetMetaData]))

(defn get-databases
  [db]
  (sql/with-connection db
    (sql/with-query-results res
      ["SELECT datname FROM pg_database;"]
      (sort (map :datname (vec res))))))

(defn get-tables
  [db]
  (sql/with-connection db
    (sql/with-query-results res
      ["SELECT * FROM information_schema.tables
        WHERE table_schema NOT IN ('information_schema', 'pg_catalog')"]
      (map :table_name (vec res)))))


(defn describe-table
  [db table]
  (sql/with-connection db
    (sql/with-query-results res
      [(format "select * from INFORMATION_SCHEMA.COLUMNS where table_name = '%s'"
               table)]
      (vec res))))
