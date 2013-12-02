(ns utils.jvm
  (:import (java.io IOException RandomAccessFile File))
  (:import (com.carrotsearch.sizeof RamUsageEstimator))
  (:import (org.github.jamm MemoryMeter))
  (:import (com.sun.tools.attach VirtualMachine)))

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

;;; http://lucene.apache.org/core/4_3_1/core/index.html?org/apache/lucene/util/RamUsageEstimator.html
;;; http://lucene.apache.org/core/3_6_2/api/core/index.html?org/apache/lucene/util/RamUsageEstimator.html
(defn size-of
  [obj]
  (RamUsageEstimator/sizeOf obj))

(defn human-size-of
  [obj]
  (RamUsageEstimator/humanSizeOf obj))

;;; MemoryMeter
(defn measure
  [obj]
  (.measure (MemoryMeter.) obj))

(defn measure-deep
  [obj]
  (.measureDeep (MemoryMeter.) obj))

(defn count-children
  [obj]
  (.countChildren (MemoryMeter.) obj))

;;; 
(defn available-processors
  []
  (.availableProcessors (Runtime/getRuntime)))
