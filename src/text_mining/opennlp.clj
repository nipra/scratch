(ns text-mining.opennlp
  (:require [clojure.java [jdbc :as jdbc]]
            [clojure.pprint :as p])
  (:use [opennlp.nlp :as nlp]
        [opennlp.treebank :as bank]
        [opennlp.tools.train :as train]))

(def get-sentences (nlp/make-sentence-detector "models/en-sent.bin"))
(def tokenize (nlp/make-tokenizer "models/en-token.bin"))
(def detokenize (nlp/make-detokenizer "models/english-detokenizer.xml"))
(def pos-tag (nlp/make-pos-tagger "models/en-pos-maxent.bin"))
(def name-find (nlp/make-name-finder "models/namefind/en-ner-person.bin"))
(def chunker (bank/make-treebank-chunker "models/en-chunker.bin"))
(def cat-model (train/train-document-categorization "training/doccat.train"))
(def get-category (nlp/make-document-categorizer cat-model))

(comment
  (pprint (get-sentences "First sentence. Second sentence? Here is another one. And so on and so forth - you get the idea..."))

  (get-category "The third verse of the song was quite upbeat."))
