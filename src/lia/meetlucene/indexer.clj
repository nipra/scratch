(ns lia.meetlucene.indexer
  (:import [org.apache.lucene.index IndexWriter
            IndexWriter$MaxFieldLength]
           [org.apache.lucene.analysis.standard StandardAnalyzer]
           [org.apache.lucene.document Document Field]
           [org.apache.lucene.store FSDirectory Directory]
           [org.apache.lucene.util Version]

           [java.io File FileFilter IOException FileReader]))

(defn create-indexer
  [index-dir]
  (let [file (File. index-dir)
        directory (FSDirectory/open file)
        standard-analyzer (StandardAnalyzer. Version/LUCENE_30)]
    (IndexWriter. directory standard-analyzer true IndexWriter$MaxFieldLength/UNLIMITED)))

(defn index-docs
  [data-dir indexer]
  )

#_(defn -main [index-dir data-dir]
    (with-open [indexer (create-indexer index-dir)]
      (let [text-files-filter (TextFilesFilter.)
            num-indexed (index-docs data-dir text-files-filter indexer)]
        )))
