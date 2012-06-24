(ns search.index-pdfs
  (:require [fs.core :as fs])
  (:require [tika.clj-tika :as tika])
  (:require [utils.clucy.core :as clucy]))

(def ^{:dynamic true} *pdf-dir* "/home/nipra/Dropbox/Personal/Documents")

(def ^{:dynamic true} *index-dir* "/home/nipra/lucene-index/pdfs")

(def ^{:doc "List of various pdf fields."}
  pdf-fields
  ["_adhocreviewcycleid" "_authoremail" "_authoremaildisplayname" "_emailsubject"
   "_reviewingtoolsshownonce" "aapl:keywords" "author" "company"
   "content-encoding" "content-type" "context.jobname" "context.time"
   "context.url" "context.version" "created" "creation-date" "creator"
   "gts_pdfxconformance" "gts_pdfxversion" "id" "keywords" "last-modified"
   "licensed to" "producer" "ptex.fullbanner" "sourcemodified" "spdf" "subject"
   "text" "title" "trapped" "xmptpg:npages"])

(defn pdf?
  [file]
  (= (tika/detect-mime-type file) "application/pdf"))

(defn get-all-pdf-files
  [dir]
  (let [files-dirs (map (partial str dir "/") (fs/list-dir dir))
        files (filter #(and (fs/file? %) (pdf? %)) files-dirs)
        dirs (filter fs/directory? files-dirs)]
    (concat files (mapcat identity (map get-all-pdf-files dirs)))))

(defn get-all-pdf-files*
  [dir]
  (let [get-files-dirs (fn [d]
                         (map (partial str d "/") (fs/list-dir d)))]
    (loop [files-dirs (get-files-dirs dir)
           files (filter #(and (fs/file? %) (pdf? %)) files-dirs)
           dirs (filter fs/directory? files-dirs)]
      (if (empty? dirs)
        files
        (let [x (mapcat identity (map get-files-dirs dirs))
              y (filter #(and (fs/file? %) (pdf? %)) x)
              z (filter fs/directory? x)]
          (recur x (concat files y) z))))))

(defn get-pdf-fields
  [pdf-files]
  (sort
   (reduce (fn [x y]
             (set
              (concat (map name (keys (tika/parse y))) x)))
           []
           pdf-files)))

(defn pdf->map
  [pdf-file]
  (tika/parse pdf-file))

(defn index-pdf
  [pdf-file index-dir]
  (let [index (clucy/disk-index index-dir)
        pdf-map (pdf->map pdf-file)]
    (clucy/add index pdf-map)))

(defn index-all-pdfs
  [pdf-dir index-dir]
  (let [pdf-files (get-all-pdf-files pdf-dir)]
    (doseq [pdf-file pdf-files]
      (index-pdf pdf-file index-dir))))
