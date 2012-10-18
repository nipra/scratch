(ns utils.jvm
  (:import (java.io IOException RandomAccessFile File)))

;;; Run this to see what your JRE's open file limit is.
(defn open-file-limit-check
  []
  (loop [files []]
    (let [x (try
              (RandomAccessFile. (str "tmp" (count files)) "rw")
              (catch IOException e
                e))]
      (if (instance? IOException x)
        (do
          (println "IOException after " (count files) " open files:" (str x))
          (doseq [file files
                  n (range 0 (count files))]
            (.close file)
            (.delete (File. (str "tmp" n)))))
        (recur (conj files x))))))
