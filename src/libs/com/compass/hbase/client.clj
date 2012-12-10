(ns libs.com.compass.hbase.client
  (:refer-clojure :exclude [get])
  (:use libs.com.compass.hbase.schema)
  (:require [libs.com.compass.hbase.filters :as f])
  (:import org.apache.hadoop.hbase.util.Bytes
	   org.apache.hadoop.hbase.HBaseConfiguration
	   org.apache.hadoop.conf.Configuration
	   [org.apache.hadoop.hbase.client HTablePool HTable HTablePool$PooledHTable
	    HTable$ClientScanner Get Put Increment Delete Scan HConnectionManager]
	   [java.util.concurrent ThreadPoolExecutor ArrayBlockingQueue TimeUnit]))

;; ====================================
;; Connections
;; ====================================

(def ^:dynamic *configuration* (HBaseConfiguration/create))

(defn- get-connection []
  (HConnectionManager/getConnection *configuration*))

;; ====================================
;; Tables
;; ====================================

(def db
  "This holds the HTablePool reference for all users. Users never have to see
   this, and the HBase API does not appear to me to allow configuration in code
   nor the use of multiple databases simultaneously (configuration is driven by
   the XML config files). So we just hide this detail from the user.)"
  (atom nil))

(defn table-pool []
  (if-let [pool @db]
    pool
    (swap! db (fn [_] (HTablePool.)))))

(defn table
  "Gets an HTable from the open HTablePool by name."
  [table-name]
  (io!
   (.getTable (table-pool) (encode-value table-name :string))))

(defn as-table [ref]
  (if (or (= (type ref) HTable)
          (= (type ref) HTablePool$PooledHTable))
    ref
    (table (name ref))))

(defn release-table [table]
  (io! (.putTable (table-pool) table)))

(defmacro with-table [[var expr] & body]
  `(let [~var (as-table ~expr)]
     (let [result# (do ~@body)]
       (release-table ~var)
       result#)))

(defn- table-schema [table]
  (with-table [table table]
    (get-schema (decode-value (.getTableName table) :string))))


;; ==================================
;; SINGLE ROW GET OPERATIONS
;; ==================================

(defn make-get
  ([table schema row constraints]
     (if constraints
       (-> (Get. (encode-row schema row))
	   (f/constrain-op schema constraints))
       (Get. (encode-row schema row)))))

(defn get
  "Get primitive row elements"
  ([table row constraints]
     (with-table [table table]
       (let [schema (table-schema table)
	     g (make-get table schema row constraints)]
	 (io! (if (:all-versions constraints)
		(decode-all schema (.get table g))
		(decode-latest schema (.get table g)))))))
  ([table row]
     (get table row nil)))

;; ==================================
;; SINGLE ROW PUT OPERATIONS
;; ==================================

(defn- put-add [#^Put put schema family col value]
  (.add put
	(encode-family schema family)
	(encode-column schema family col)
	(encode-cell schema family col value)))

(defn make-put [schema row values]
;;  (println "Encoding row: " row " of type: " (type row) " for schema row type: "
  ;;	   (row-type schema))
  ;; (println "row " row)
  ;; (println "values " values)
  ;; (println "row type" (row-type schema))
  (let [rowbytes (encode-row schema row)
	put (new Put rowbytes)]
    (if (map? values)
      (doseq [[family cols] values]
	(doseq [[col value] cols]
	  (put-add put schema family col value)))
      (doseq [[family col value] values]
	(put-add put schema family col value)))
    put))

(defn put
  "Put data into a row using a value map or a vector sequence:
   of vectors.  Value maps are {:family {:column value :column value}}
   and vector inputs are [[family column value] [family column value]]
   No options are currently supported, but are maintained for future
   improvements."
  [table row values & opts]
  (with-table [table table]
    (let [schema (table-schema table) ;; NOTE: or from schema argument
	  p (make-put schema row values)]
      (io! (.put table p)))))

(defn put-one [table row family column value]
  (put table row [[family column value]]))

;; =========================================
;; SINGLE ROW DELETES
;; =========================================

(defn make-del [schema row]
  (Delete. (encode-row schema row)))

(defn add-del-family [del schema family]
  (doto del
    (.deleteFamily (encode-family schema family))))

(defn add-del-column [del schema family column]
  (doto del
    (.deleteColumns (encode-family schema family)
		    (encode-column schema family column))))

(defn do-del [table del]
  (with-table [table table]
    (io! (.delete table del))))

(defn del
  "Directly delete all or part of a row (all versions)"
  ([table row]
     (with-table [table table]
       (let [schema (table-schema table)]
	 (do-del table (make-del schema row)))))
  ([table row family]
     (with-table [table table]
       (let [schema (table-schema table)]
	 (do-del table (-> (make-del schema row)
			   (add-del-family schema family))))))
  ([table row family column]
     (with-table [table table]
       (let [schema (table-schema table)]
	 (do-del table (-> (make-del schema row)
                       (add-del-column schema family column)))))))

;; =========================================
;; Increment operation

;; (c/define-schema :items [:defaults [:keyword :long]
;;                        :row-type :integer]
;;    :counters [:keyword :long])
;; (client/increment :items 100 {:counters {:downvote -2 upvote: 1}})
;; =========================================

(defn- increment-add [#^Increment increment schema family col value]
  (.addColumn increment
              (encode-family schema family)
              (encode-column schema family col)
              (long value)))

(defn make-increment [schema row values]
  (let [rowbytes (encode-row schema row)
        increment (new Increment rowbytes)]
    (if (map? values)
      (doseq [[family cols] values]
        (doseq [[col value] cols]
          (increment-add increment schema family col value)))
      (doseq [[family col value] values]
        (increment-add increment schema family col value)))
    increment))

(defn increment
  "Increment data into a row using a value map or a vector sequence:
   of vectors.  Value maps are {:family {:column value :column value}}
   and vector in increment are [[family column value] [family column value]]"
  ([table row values constraints]
     (with-table [table table]
       (let [schema (table-schema table)
             i (make-increment schema row values)]
         (io! (if (:all-versions constraints)
                (decode-all schema (.increment table i))
                (decode-latest schema (.increment table i)))))))
  ([table row values]
     (increment table row values nil)))

(defn increment-one [table row family column value]
  (increment table row [[family column value]]))

;; =========================================
;; Multi Row Get / Put operations
;; =========================================

(def multi-action-executor (atom nil))

(defn- get-action-executor []
  (if-let [exec @multi-action-executor]
    exec
    (let [start-pool 10
	  max-pool 20
	  keepalive 60
	  queue (ArrayBlockingQueue. 40)]
      (swap! multi-action-executor
	     (fn [a e] e)
	     (ThreadPoolExecutor. start-pool max-pool keepalive
				  TimeUnit/SECONDS queue)))))

(defn process-batch
  "Low level execution of batch commands"
  [table actions]
  (let [results (make-array java.lang.Object (count actions))]
    (let [exec (get-action-executor)]
      (println (format "Executor: complete:%s active:%s (core:%s cursize:%s qsize:%s)"
		       (.getCompletedTaskCount exec) (.getActiveCount exec)
		       (.getCorePoolSize exec) (.getPoolSize exec)
		       (.size (.getQueue exec))))
      (io! (.processBatch (get-connection)
			  actions
			  (encode-value table :string)
			  exec
			  results)))
    results))

(defn make-puts
  "Like make put, but accepts vectors of arguments to "
  [schema records]
  (doall
   (map #(make-put schema (first %) (second %))
	records)))

(defn put-multi
  "Give a table reference and a sequence of value vectors of the form
   [[row <values>] [row <values>]] where <values> = {:family {:col <value>} ...} |
   [[family col value] [family col value] ...].  Performs a single
   batch of actions using a fixed thread pool."
  [table records]
  (let [schema (table-schema table)
	puts (make-puts schema records)]
    (map (partial decode-latest schema)
	 (process-batch table puts))))

(defn make-gets
  ([table schema records]
     (map (fn [rec]
	    (if (sequential? rec)
	      (make-get table schema (first rec) (second rec))
	      (make-get table schema rec nil)))
	  records))
  ([table schema records constraints]
      (map (fn [rec]
	     (make-get table schema rec constraints))
	   records)))

(defn get-multi
  "Similar to put multi, except the input records are [[row <options>] ...]
   where <options> is a flat list of keyvalue pairs suitable to pass to make-get"
  ([table records]
     (let [schema (table-schema table)
	   gets (make-gets table schema records)]
       (doall
	(map (partial decode-latest schema)
	     (process-batch table gets)))))
  ([table records common-constraints]
     (let [schema (table-schema table)
	   gets (make-gets table schema records common-constraints)]
       (doall
	(map (partial decode-latest schema)
	     (process-batch table gets))))))


;; ==================================
;; Scanning
;; ==================================

;; Scan ranges of tables
;; 1. Map over a set of elements
;; 2. Return all the elements
;; 3. Procedural filtering elements

(def cache-block-size 100)

(defn make-scan [schema constraints]
  (let [scan (Scan.)]
    (.setCaching scan cache-block-size)
    (f/constrain-op scan schema constraints)))

(defn scan
  "Apply filter/processing function fn to all entries in the
   table as constrained by the optional filter object.  fn receives
   the decoded row and a family map of values for that row"
  ([fn table constraints all?]
     (with-table [table table]
       (let [schema (table-schema table)
	     scan (make-scan schema constraints)
	     scanner (io! (.getScanner table scan))
	     decoder (if all? decode-all decode-latest)
	     results (doall
		      (keep #(apply fn (decoder schema %))
			    scanner))]
	 (.close scanner)
	 results)))
  ([fn table constraints]
     (scan fn table constraints nil))
  ([fn table]
     (scan fn table (f/constraints) nil)))

(defn do-scan
  "Do scan will run a function over the returned results without
   collecting them (presumably for side effects)"
  ([fn table constraints]
     (with-table [table table]
       (let [schema (get-schema table)
	     scan (make-scan schema constraints)
	     scanner (io! (.getScanner table scan))]
	 (doseq [result scanner]
	   (apply fn (decode-latest schema result)))
	 (.close scanner)
	 nil))))


(defn raw-scan
  "This function collects the scan results without decoding
   The function can filter results by returning nil"
  ([fn table constraints]
     (with-table [table table]
       (let [schema (get-schema table)
	     scan (make-scan schema constraints)
	     scanner (io! (.getScanner table scan))
	     results (doall
		      (keep fn scanner))]
	 (.close scanner)
	 results))))

;; All de-referencing actions must take
;; place within a scanner context
;; (defn record-scanner
;;   "Like duck read-lines; a lazy sequence that reads hbase records
;;    until it reaches the end and closes the scanner"
;;   [table & filter]
;;   (let [read-record (fn this [^HTable$ClientScanner scan]
;; 		      (lazy-seq
;; 		       (if-let [record (.next scan)]
;; 			 (cons (translate-result record) (this scan))
;; 			 (.close scan))))]
;;     (read-record
;;      (hbase/scanner (as-table table)
;; 		    (make-scan filter)))))


