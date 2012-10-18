(ns search.lucene.term-frequency
  (:require [clojure.pprint :as p])
  (:require [utils.clucy.core :as clucy])
  (:import (java.io StringReader File)
           (org.apache.lucene.analysis.standard StandardAnalyzer)
           (org.apache.lucene.document Document Field Field$Index Field$Store Field$TermVector)
           (org.apache.lucene.index IndexReader IndexWriter IndexWriter$MaxFieldLength Term)
           (org.apache.lucene.queryParser QueryParser)
           (org.apache.lucene.search BooleanClause BooleanClause$Occur
                                     BooleanQuery IndexSearcher TermQuery)
           (org.apache.lucene.search.highlight Highlighter QueryScorer
                                               SimpleHTMLFormatter)
           (org.apache.lucene.store NIOFSDirectory RAMDirectory)
           (org.apache.lucene.util Version)
           (org.apache.lucene.analysis Token WhitespaceAnalyzer SimpleAnalyzer
                                       StopAnalyzer)
           (org.apache.lucene.analysis.tokenattributes
            TermAttribute PositionIncrementAttribute OffsetAttribute
            TypeAttribute)))

(defn get-tokens
  [analyzer text]
  (let [token-stream (.tokenStream analyzer
                                   "contents" (StringReader. text))
        term (.addAttribute token-stream TermAttribute)]
    
    (loop [tokens []]
      (if (.incrementToken token-stream)
        (recur (conj tokens (.term term)))
        tokens))))

(defn get-term-freq [value]
  (let [analyzer (StandardAnalyzer. Version/LUCENE_36)
        directory (RAMDirectory.)
        field "tmp"]
    (with-open [writer (IndexWriter. directory analyzer IndexWriter$MaxFieldLength/UNLIMITED)]
      (let [doc (Document.)
            field (Field. "tmp" value Field$Store/YES Field$Index/ANALYZED Field$TermVector/YES)
            _ (.add doc field)
            _ (.addDocument writer doc)]))
    (let [reader (IndexReader/open directory)
          searcher (IndexSearcher. reader)
          parser (QueryParser. Version/LUCENE_36 field analyzer)
          term (first (get-tokens analyzer value))
          query (.parse parser (format "%s:%s" field term))
          hits (.search searcher query 1)]
      (let [hit (first (.scoreDocs hits))
            tfv (.getTermFreqVector reader (.doc hit) field)]
        (zipmap (seq (.getTerms tfv))
                (seq (.getTermFrequencies tfv)))))))

(comment
  (clojure.pprint/pprint
   (get-term-freq "The Apache Software Foundation provides support for the Apache community of open-source software projects. The Apache projects are defined by collaborative consensus based processes, an open, pragmatic software license and a desire to create high quality software that leads the way in its field. Apache Lucene, Apache Solr, Apache PyLucene, Apache Open Relevance Project and their respective logos are trademarks of The Apache Software Foundation. All other marks mentioned may be trademarks or registered trademarks of their respective owners.")))
