(ns utils.pdf
  (:import [org.apache.pdfbox.pdmodel PDDocument]))

;;; http://svn.apache.org/viewvc/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/fdf/PrintFields.java?view=markup
(defn get-fields
  [file]
  (let [pdd (PDDocument/load file)
        fields (.getFields (.getAcroForm (.getDocumentCatalog pdd)))
        iter (.iterator fields)]
    (loop [fields []]
      (if (.hasNext iter)
        (recur (conj fields (.getPartialName (.next iter))))
        fields))))
