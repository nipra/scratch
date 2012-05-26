(ns xml
  (:import [javax.xml.xpath XPathFactory XPath XPathConstants]
           [org.xml.sax InputSource]))

(def *url "http://localhost:8983/solr/core0/admin/stats.jsp")

(def *xml (slurp *url))

(def *input-source (InputSource. (clojure.java.io/reader *url)))

(def *xpath (.newXPath (XPathFactory/newInstance)))

(def *nodes (.evaluate *xpath "/solr/core/text()" *input-source XPathConstants/NODESET))

(def *value (.getNodeValue (.item *nodes 0)))

(def *node-set (.getDTMIterator *nodes))

(comment
  (let [input-source (InputSource. (clojure.java.io/reader *url))
        xpath (.newXPath (XPathFactory/newInstance))
        nodes (.evaluate xpath "//entry/name/text()" input-source XPathConstants/NODESET)
        desired-caches #{"queryResultCache" "documentCache" "filterCache"}]
    (doall
     (remove nil?
             (for [n (range (.getLength nodes))
                   :let [entry-name (clojure.string/trim (.getNodeValue (.item nodes n)))]]
               (desired-caches entry-name))))
    nodes))


