(ns search.lucene.analysis
  (:import [org.apache.lucene.analysis
            Token WhitespaceAnalyzer SimpleAnalyzer StopAnalyzer]
           [org.apache.lucene.analysis.standard StandardAnalyzer]
           [org.apache.lucene.analysis.tokenattributes
            TermAttribute PositionIncrementAttribute OffsetAttribute
            TypeAttribute]
           [org.apache.lucene.util Version]
           [java.io StringReader])
  (:require [clojure.pprint :as p]))

(def *version-current* Version/LUCENE_CURRENT)
(def *version-34* Version/LUCENE_34)

(def *whitespace-analyzer* (WhitespaceAnalyzer.))
(def *simple-analyzer* (SimpleAnalyzer.))
(def *stop-analyzer* (StopAnalyzer. *version-34*))
(def *standard-analyzer* (StandardAnalyzer. *version-34*))

(def *analyzers* [*whitespace-analyzer*
                  *simple-analyzer*
                  *stop-analyzer*
                  *standard-analyzer*])

(defn get-tokens
  [analyzer text]
  (let [token-stream (.tokenStream analyzer
                                   "contents" (StringReader. text))
        term (.addAttribute token-stream TermAttribute)]
    
    (loop [tokens []]
      (if (.incrementToken token-stream)
        (recur (conj tokens (.term term)))
        tokens))))


(defn get-tokens-with-details
  [analyzer text & {:keys [compact?] :or {compact? false}}]
  (let [token-stream (.tokenStream analyzer
                                   "contents" (StringReader. text))
        term (.addAttribute token-stream TermAttribute)
        pos-incr (.addAttribute token-stream PositionIncrementAttribute)
        offset (.addAttribute token-stream OffsetAttribute)
        type (.addAttribute token-stream TypeAttribute)
        tokens (loop [tokens []
                      position 0]
                 (if (.incrementToken token-stream)
                   (let [increment (.getPositionIncrement pos-incr)
                         new-position (if (> increment 0)
                                        (+ position increment))
                         token {:term (.term term)
                                :position new-position
                                :start (.startOffset offset)
                                :end (.endOffset offset)
                                :type (.type type)}]
                     (recur (conj tokens token) new-position))
                   tokens))]
    (if compact?
      (for [token tokens]
        (format "%s: [%s:%s->%s:%s]"
                (:position token) (:term token) (:start token) (:end token)
                (:type token)))
      tokens)))


(comment
  (p/pprint
   (for [analyzer *analyzers*]
     [(.. analyzer getClass getSimpleName)
      (get-tokens analyzer "foo bar") ]))

  (p/pprint
   (for [analyzer *analyzers*]
     [(.. analyzer getClass getSimpleName)
      (get-tokens-with-details analyzer "Active Cultures: Linking Value and Digital Marketing as Told Through Yogurt")])))
