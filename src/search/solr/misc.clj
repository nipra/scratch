(ns search.solr.misc
  (:import (org.apache.solr.client.solrj.impl CommonsHttpSolrServer)
           (org.apache.solr.common.params ModifiableSolrParams)
           [org.apache.solr.client.solrj.response QueryResponse]
           [org.apache.lucene.queryParser QueryParserConstants]
           [org.apache.solr.common SolrInputDocument]
           [org.apache.solr.client.solrj SolrQuery]
           [org.apache.solr.client.solrj.request QueryRequest])
  (:require [clojure.pprint :as p])
  (:require [utils.solr.core :as core])
  (:require [utils.solr.nipra.core :as c]))

(comment
  (def *url "http://localhost:8983/solr/core0")

  (def *solr (CommonsHttpSolrServer. *url))

  (def *doc (SolrInputDocument.))

  (let [url "http://localhost:8983/solr/core0"
        solr (CommonsHttpSolrServer. *url)
        query (SolrQuery.)
        _ (doto query
            (.setQueryType "/terms")
            (.setTerms true)
            (.setTermsLimit 5)
            (.setTermsLower "s")
            (.setTermsPrefix "s")
            (.addTermsField "title")
            (.setTermsMinCount 1))
        request (QueryRequest. query)]
    (map (fn [x]
           (.getTerm x))
         (.getTerms (.getTermsResponse (.process request solr)) "title")))

  (let [url "http://index07:8983/solr/core0"
        solr (CommonsHttpSolrServer. *url)
        query (SolrQuery.)
        _ (doto query
            (.setQueryType "/terms")
            (.setTerms true)
            (.addTermsField "title"))
        request (QueryRequest. query)]
    (map (fn [x]
           (.getTerm x))
         (.getTerms (.getTermsResponse (.process request solr)) "title"))))
