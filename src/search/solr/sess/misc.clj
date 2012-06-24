(ns search.solr.sess.misc
  (:require [utils.solr.nipra.core :as solr]))

(comment
  (solr/search "http://localhost:8983/solr/mbtracks"
               {"rows" 1
                "q" "*:*"
                "debugQuery" true}))
