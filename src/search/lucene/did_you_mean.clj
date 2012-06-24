(ns search.lucene.did-you-mean
  (:require [clojure.pprint :as p])
  (:require [search.lucene.analysis :as analysis])
  (:import [org.apache.lucene.search.spell LevensteinDistance])
  (:import [org.apache.lucene.search.spell LevensteinDistance])
  (:import [org.apache.lucene.store FSDirectory])

  (:import (java.io StringReader File)
           (org.apache.lucene.analysis.standard StandardAnalyzer)
           (org.apache.lucene.analysis Analyzer SimpleAnalyzer)
           (org.apache.lucene.document Document Field Field$Index Field$Store)
           (org.apache.lucene.index IndexWriter IndexWriter$MaxFieldLength Term IndexReader)
           (org.apache.lucene.queryParser QueryParser)
           (org.apache.lucene.search BooleanClause BooleanClause$Occur
                                     BooleanQuery IndexSearcher TermQuery)
           (org.apache.lucene.search.highlight Highlighter QueryScorer
                                               SimpleHTMLFormatter
                                               TokenSources)
           (org.apache.lucene.store NIOFSDirectory RAMDirectory)
           (org.apache.lucene.util Version))
  
  (:import [java.io File])
  (:import [java.net URI]))

(def *version* Version/LUCENE_CURRENT)

(def *l (LevensteinDistance.))

(def *words ["point"
             "print"
             "paint"
             "pinto"
             "pink"
             "int"
             "pin"
             "mint"
             "pont"
             "pit"
             "pine"
             "ping"
             "pino"
             "punt"
             "hint"
             "sint"
             "pins"
             "piet"
             "pant"
             "pitt"
             "joint"
             "saint"
             "pines"
             "ppt"
             "part"
             "into"
             "post"
             "want"
             "pain"
             "past"
             "port"
             "pet"
             "dont"
             "rent"
             "pics"
             "pvt"
             "sant"
             "pie"
             "put"
             "pix"
             "hunt"
             "pic"
             "pick"
             "ant"
             "piel"
             "plot"
             "pat"
             "kent"
             "pot"
             "cent"
             "font"
             "pipe"
             "pst"
             "cont"
             "pill"
             "spin"
             "pisa"
             "penn"
             "pig"
             "kant"
             "pest"
             "intl"
             "mont"
             "poin"
             "cant"
             "pico"
             "prin"
             "pio"
             "ent"
             "inst"
             "piso"
             "pert"
             "più"
             "tent"
             "pip"
             "pim"
             "phat"
             "pigs"
             "pies"
             "pib"
             "went"
             "pia"
             "gent"
             "ptt"
             "lent"
             "pii"
             "sent"
             "vent"
             "sont"
             "tnt"
             "pct"
             "pier"
             "pimp"
             "pips"
             "pis"
             "wont"
             "pdt"
             "pwpt"
             "piu"
             "plat"])

(def *words2 ["pink" "pin" "pont" "pit" "pine"])

(def *f (File. "file:///home/nipra"))

(comment
  (p/pprint
   (reverse
    (sort-by second
             (for [word *words]
               [word (.getDistance *l "pint" word) ])))))


;;;
(def *index-dir*)

(comment
  (def *f (FSDirectory/open (File. *index-dir*)))
  (seq (.listAll *f))
  (.close *f))

(defn doc->map
  [doc]
  (into {} (for [f (.getFields doc)]
             [(keyword (.name f)) (.stringValue f)])))

(comment
  (with-open [dir (FSDirectory/open (File. *index-dir*))]
    (with-open [index-reader (IndexReader/open dir)]
      (with-open [searcher (IndexSearcher. index-reader)]
        (let [term (Term. "title" "python")
              query (TermQuery. term)
              top-docs (.search searcher query 1)]
          (doall
           (for [score-doc (.scoreDocs top-docs)
                 :let [score (.score score-doc)
                       doc-id (.doc score-doc)]]
             {:doc (doc->map (.doc searcher doc-id))
              :score score
              :doc-id doc-id})))))))

(comment
  (let [query-parser (QueryParser. *version* "contents" (SimpleAnalyzer.))
        query (.parse query-parser "python")]
    query)

  (let [query-parser (QueryParser. *version* "" (SimpleAnalyzer.))
        query (.parse query-parser "title:python")
        top-docs (.search *searcher query 1)]
    (doall
     (for [score-docs (.scoreDocs top-docs)
           :let [score (.score score-docs)]]
       {:doc (doc->map (.doc *searcher (.doc score-docs)))
        :score score}))))

(comment
  (def *dir (FSDirectory/open (File. *index-dir*)))
  (def *index-reader (IndexReader/open dir))
  (def *token-sources (TokenSources.))
  (def *ts (TokenSources/getTokenStream *index-reader *doc-id "title"
                                        search.lucene.analysis/*standard-analyzer*)))
