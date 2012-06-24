(ns search.lucene.highlighting
  (:require [clojure.pprint :as p])
  (:import [org.apache.lucene.search.spell LevensteinDistance])
  (:import [org.apache.lucene.search.spell LevensteinDistance])
  (:import [org.apache.lucene.store FSDirectory])

  (:import (java.io StringReader File)
           (org.apache.lucene.analysis.standard StandardAnalyzer)
           (org.apache.lucene.analysis SimpleAnalyzer)
           (org.apache.lucene.document Document Field Field$Index Field$Store)
           (org.apache.lucene.index IndexWriter IndexWriter$MaxFieldLength Term IndexReader)
           (org.apache.lucene.queryParser QueryParser)
           (org.apache.lucene.search BooleanClause BooleanClause$Occur
                                     BooleanQuery IndexSearcher TermQuery
                                     DisjunctionMaxQuery)
           (org.apache.lucene.search.highlight Highlighter QueryScorer
                                               SimpleHTMLFormatter
                                               SimpleSpanFragmenter
                                               TokenSources)
           (org.apache.lucene.store NIOFSDirectory RAMDirectory)
           (org.apache.lucene.util Version))
  
  (:import [java.io File])
  (:import [java.net URI]))


(comment
  (let [text "The quick brown fox jumps over the lazy dog"
        query (TermQuery. (Term. "field" "fox"))
        token-stream (.tokenStream (SimpleAnalyzer.)
                                   "field"
                                   (StringReader. text))
        scorer (QueryScorer. query "field")
        fragmenter (SimpleSpanFragmenter. scorer)
        highlighter (Highlighter. scorer)]
    (.setTextFragmenter highlighter fragmenter)
    (.getBestFragment highlighter token-stream text)))

(comment
  (let [text (str "In this section we'll show you how to make the simplest "
                  "programmatic query, searching for a single term, and then "
                  "we'll see how to use QueryParser to accept textual queries. "
                  "In the sections that follow, we’ll take this simple example "
                  "further by detailing all the query types built into Lucene. "
                  "We begin with the simplest search of all: searching for all "
                  "documents that contain a single term.")
        search-text "term"
        parser (QueryParser. Version/LUCENE_CURRENT "f"
                             (StandardAnalyzer. Version/LUCENE_CURRENT))
        query (.parse parser search-text)
        formatter (SimpleHTMLFormatter. "<span class=\"highlight\">", "</span>")
        token-stream (.tokenStream (StandardAnalyzer. Version/LUCENE_CURRENT)
                                   "f"
                                   (StringReader. text))
        scorer (QueryScorer. query "f")
        highlighter (Highlighter. formatter scorer)]
    (.setTextFragmenter highlighter (SimpleSpanFragmenter. scorer))
    (.getBestFragments highlighter token-stream text 3 "...")))

(comment
  (def *index-dir*)
  (def *dir* (FSDirectory/open (File. *index-dir*)))
  (def *index-reader* (IndexReader/open *dir*))
  (def *searcher* (IndexSearcher. *index-reader*)))

(comment
  (let [query (TermQuery. (Term. "title" "social"))
        hits (.search *searcher* query 10)
        scorer (QueryScorer. query "title")
        highlighter (Highlighter. scorer)
        _ (.setTextFragmenter highlighter (SimpleSpanFragmenter. scorer))
        analyzer (SimpleAnalyzer.)]
    (doall
     (for [score-doc (.scoreDocs hits)
           :let [doc-id (.doc score-doc)
                 doc (.doc *searcher* doc-id)
                 title (.get doc "title")
                 stream (TokenSources/getAnyTokenStream
                         (.getIndexReader *searcher*)
                         (.doc score-doc)
                         "title"
                         doc
                         analyzer)
                 fragment (.getBestFragment highlighter stream title)]]
       fragment))))


(comment
  (let [query (DisjunctionMaxQuery. 0)
        tq1 (TermQuery. (Term. "title" "ruby"))
        _ (.setBoost tq1 1)
        _ (.add query tq1)
        tq2 (TermQuery. (Term. "title" "block"))
        _ (.setBoost tq2 1)
        _ (.add query tq2)
        hits (.search *searcher* query 10)
        scorer (QueryScorer. query "title")
        highlighter (Highlighter. scorer)
        _ (.setTextFragmenter highlighter (SimpleSpanFragmenter. scorer))
        analyzer (SimpleAnalyzer.)]
    (doall
     (for [score-doc (.scoreDocs hits)
           :let [doc-id (.doc score-doc)
                 doc (.doc *searcher* doc-id)
                 title (.get doc "title")
                 stream (TokenSources/getAnyTokenStream
                         (.getIndexReader *searcher*)
                         (.doc score-doc)
                         "title"
                         doc
                         analyzer)
                 fragment (.getBestFragment highlighter stream title)]]
       fragment))))
