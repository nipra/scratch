(ns so.db
  (:use [korma.db]
        [korma.core])
  (:require [clojure.java [jdbc :as jdbc]]
            [clojure.pprint :as p]))

(def conn
  (connection-pool {:classname "org.sqlite.JDBC"
                    :subprotocol "sqlite"
                    :subname "/home/nipra/sqlite/so-dump.db"}))

(comment
  (jdbc/with-connection conn
    (jdbc/with-query-results rows
      ["select * from posts limit 1"]
      (doall rows)))

  (jdbc/with-connection conn
    (jdbc/with-query-results rows
      ["select * from posts where title = 'What is a metaclass in Python?'
        limit 1"]
      (doall rows)))

  (jdbc/with-connection conn
    (jdbc/with-query-results rows
      ["PRAGMA INDEX_LIST('posts')"]
      (doall rows))))
