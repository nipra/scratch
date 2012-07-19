(ns freebase.db
  (:require [utils.postgres :as pg])
  (:use [korma.db]
        [korma.core])
  (:require [clojure.java [jdbc :as jdbc]]
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


;;;
(def db-conn
  (connection-pool {:classname "org.postgresql.Driver"
                    :subprotocol "postgresql"
                    :subname "//localhost/wikipedia"
                    :user "postgres"
                    :password "postgres"
                    :db "wikipedia"}))

(comment
  (jdbc/with-connection db-conn
    (jdbc/with-query-results rows
      ["select count(*) from articles"]
      (first rows)))
  (jdbc/with-connection db-conn
    (jdbc/with-query-results rows
      ["select name from articles
        where to_tsvector(name) @@ to_tsquery('lost')
        limit 10"]
      (doall rows))))
