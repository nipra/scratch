(ns utils.solr.nipra.core
  (:import (org.apache.solr.client.solrj.impl CommonsHttpSolrServer)
           (org.apache.solr.common SolrInputDocument)
           (org.apache.solr.client.solrj SolrQuery)
           (org.apache.solr.common.params ModifiableSolrParams)
           [org.apache.solr.client.solrj.request UpdateRequest]
           [org.apache.lucene.queryParser QueryParserConstants])
  (:require [clojure.pprint :as p])
  (:require [utils.solr.core :as core])
  (:require [utils.solr.nipra.core :as c]))

(defn make-document [doc]
  (let [solr-doc (SolrInputDocument.)]
    (doseq [[key value] doc]
      (let [key (cond
                 (keyword? key) (name key)
                 :default (str key))]
        (.addField solr-doc key value)))
    solr-doc))


(defn add-document [server doc]
  (.add server (make-document doc)))

(defn add-documents [server coll]
  (.add server (to-array (map make-document coll))))

(defn commit [server]
  (.commit server))

(defn server
  [url]
  (CommonsHttpSolrServer. url))

(defn delete-by-id [server id]
  (.deleteById server (str id)))

(defn delete-by-ids [server ids]
  (.deleteById server (map str ids)))

(defn make-solr-params
  [params-map]
  (let [params (ModifiableSolrParams.)]
    (dorun
     (for [[field value] params-map]
       (if (vector? value)
         (.set params field (into-array String value))
         (.set params field value))))
    params))

;;; solr.exploration
(defn get-results
  [response]
  (map core/doc-to-hash (.getResults response)))

(defn convert-misspelling
  [misspelling]
  {"correction" (.getCorrection misspelling)
   "original" (.getOriginal misspelling)})

(defn convert-collated-result
  [coll-result]
  {"collation-query-string" (.getCollationQueryString coll-result)
   "number-of-hits" (.getNumberOfHits coll-result)
   "misspellings-and-corrections" (map convert-misspelling (.getMisspellingsAndCorrections coll-result))})

(defn convert-suggestion-map
  [suggestion-map]
  (doall
   (for [[k v] suggestion-map]
     {"query-part" k
      "suggestion" {"alternatives" (vec (.getAlternatives v))
                    "end-offset" (.getEndOffset v)
                    "start-offset" (.getStartOffset v)
                    "num-found" (.getNumFound v)
                    "original-frequency" (.getOriginalFrequency v)
                    "token" (.getToken v)
                    "alternative-frequencies" (vec (.getAlternativeFrequencies v))}})))

(defn search
  [url params & [{pp :pp :or {pp true}}]]
  (let [solr (CommonsHttpSolrServer. url)
        solr-params (ModifiableSolrParams.)]
    (dorun
     (for [[k v] params]
       (.set solr-params k (core/make-param v))))
    (let [response (.query solr solr-params)
          results (map core/doc-to-hash (.getResults response))
          debug-map (into {} (.getDebugMap response))
          debug-map2 (-> debug-map
                         (update-in ["timing"] #(into {} %))
                         (update-in ["timing" "prepare"] #(into {} %))
                         (update-in ["timing" "process"] #(into {} %)))
          explain-map (.getExplainMap response)
          spell-check-response (when-let [x (.getSpellCheckResponse response)]
                                 {"suggestion-map" (convert-suggestion-map (.getSuggestionMap x))
                                  "collated-results" (map convert-collated-result (.getCollatedResults x))
                                  "is-correctly-spelled" (.isCorrectlySpelled x)})
          highlighting (into {}
                             (for [[k v] (.getHighlighting response)]
                               [k (into {}
                                        (for [[x y] v]
                                          [x (vec y)]))]))
          final-results (merge {"results" results}
                               {"debug-map" debug-map2
                                "explain-map" (into {} explain-map)
                                "spell-check-response" spell-check-response
                                "highlighting" highlighting})]

      ;; (println pp)
      ;; (if pp
      ;;   (p/pprint final-results)
      ;;   final-results)
      {:results final-results 
       :response response}
      )))

(comment
  (def *local "http://localhost:8983/solr/core0")
  (p/pprint
   (search *local
           {"rows" 1
            "q" "*:*"
            "fq" "indexed_at:[* TO 2012-01-03T00:00:00Z]"
            "fl" "indexed_at,title,created_at"
            "debugQuery" true})))
