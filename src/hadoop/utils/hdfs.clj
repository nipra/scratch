(ns hadoop.utils.hdfs
  (:import (org.apache.hadoop.conf Configuration))
  (:import (org.apache.hadoop.fs FileSystem Path))
  (:import (org.apache.hadoop.io IOUtils))
  (:import (org.apache.hadoop.util Progressable))
  (:import (java.net URI))
  (:import (java.io File FileOutputStream
                    FileInputStream InputStream BufferedInputStream))
  (:require (clojure.java [io :as io]))
  (:require (clojure [pprint :as p])))

(defn read-file
  [source-uri]
  (let [conf (Configuration.)
        fs (FileSystem/get (URI/create source-uri) conf)
        in (.open fs (Path. source-uri))]
    (try
      (apply str (interpose "\n" (line-seq  (io/reader in))))
      (finally (IOUtils/closeStream in)))))

(defn copy-to-local
  [source-uri local-file]
  (let [conf (Configuration.)
        fs (FileSystem/get (URI/create source-uri) conf)
        in (.open fs (Path. source-uri))]
    (try
      (IOUtils/copyBytes in (FileOutputStream. local-file) 4096)
      (finally (IOUtils/closeStream in)))))

;;; FIXME: Not working
;;; hadoop-book/ch03/src/main/java/FileCopyWithProgress.java
(defn copy-from-local
  [destination-uri local-file]
  (let [in (BufferedInputStream. (FileInputStream. local-file))
        conf (Configuration.)
        fs (FileSystem/get (URI/create destination-uri) conf)
        out (.create fs (Path. destination-uri))]
    (try
      (IOUtils/copyBytes in out 4096)
      (finally (IOUtils/closeStream in)))))
