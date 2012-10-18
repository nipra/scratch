(ns search.lucene.check-index
  (:import [org.apache.lucene.index CheckIndex])
  (:require [utils.clucy.core :as clucy]))

(defn get-segment-info-details
  [segment-info]
  (let [field-norm-status (.fieldNormStatus segment-info)
        stored-field-status (.storedFieldStatus segment-info)
        term-index-status (.termIndexStatus segment-info)
        term-vector-status (.termVectorStatus segment-info)]
    {:compound {:value (.compound segment-info)
                :doc "True if segment is compound file format."}
     :deletions-file-name {:value (.deletionsFileName segment-info)
                           :doc "Name of the current deletions file name."}
     :diagnostics {:value (into {} (.diagnostics segment-info))
                   :doc "Map that includes certain debugging details that IndexWriter records into each segment it creates"}
     :doc-count {:value (.docCount segment-info)
                 :doc "Document count (does not take deletions into account)."}
     :doc-store-compound-file {:value (.docStoreCompoundFile segment-info)
                               :doc "True if the shared doc store files are compound file format."}
     :doc-store-offset {:value (.docStoreOffset segment-info)
                        :doc "Doc store offset, if this segment shares the doc store files (stored fields and term vectors) with other segments."}
     :doc-store-segment {:value (.docStoreSegment segment-info)
                         :doc "String of the shared doc store segment, or null if this segment does not share the doc store files."}
     :field-norm-status (when field-norm-status
                          {:value {:tot-fields {:value (.totFields field-norm-status)
                                                :doc "Number of fields successfully tested"}
                                   :error {:value (.error field-norm-status)
                                           :doc "Exception thrown during term index test (null on success)"}}
                           :doc "Status for testing of field norms (null if field norms could not be tested)."})
     :has-deletions {:value (.hasDeletions segment-info)
                     :doc "True if this segment has pending deletions."}
     :has-prox {:value (.hasProx segment-info)
                :doc "True if at least one of the fields in this segment has position data"}
     :name {:value (.name segment-info)
            :doc "Name of the segment."}
     :num-deleted {:value (.numDeleted segment-info)
                   :doc "Number of deleted documents."}
     :num-files {:value (.numFiles segment-info)
                 :doc "Number of files referenced by this segment."}
     :open-reader-passed {:value (.openReaderPassed segment-info)
                          :doc "True if we were able to open a SegmentReader on this segment."}
     :size-MB {:value (.sizeMB segment-info)
               :doc "Net size (MB) of the files referenced by this segment."}
     :stored-field-status (when stored-field-status
                            {:value {:doc-count {:value (.docCount stored-field-status)
                                                 :doc "Number of documents tested."}
                                     :error {:value (.error stored-field-status)
                                             :doc "Exception thrown during stored fields test (null on success)"}
                                     :tot-fields {:value (.totFields stored-field-status)
                                                  :doc "Total number of stored fields tested."}}
                             :doc "Status for testing of stored fields (null if stored fields could not be tested)."})
     :term-index-status (when term-index-status
                          {:value {:error {:value (.error term-index-status)
                                           :doc "Exception thrown during term index test (null on success)"}
                                   :term-count {:value (.termCount term-index-status)
                                                :doc "Total term count"}
                                   :tot-freq {:value (.totFreq term-index-status)
                                              :doc "Total frequency across all terms."}
                                   :tot-pos {:value (.totPos term-index-status)
                                             :doc "Total number of positions."}}
                           :doc "Status for testing of indexed terms (null if indexed terms could not be tested)."})
     :term-vector-status (when term-vector-status
                           {:doc-count {:value (.docCount term-vector-status)
                                        :doc "Number of documents tested."}
                            :error {:value (.error term-vector-status)
                                    :doc "Exception thrown during term vector test (null on success)"}
                            :tot-vectors {:value (.totVectors term-vector-status)
                                          :doc "Total number of term vectors tested."}})}))


(defn check-index
  [index-dir]
  (let [status (.checkIndex (CheckIndex. (clucy/disk-index index-dir)))
        dir (.dir status)]
    {:cant-open-segments {:value (.cantOpenSegments status)
                          :doc "True if we were unable to open the segments_N file."}
     :clean {:value (.clean status)
             :doc "True if no problems were found with the index."}
     :all-files {:value (sort (seq (.listAll dir)))
                 :doc "List of all files in index directory."}
     :index-directory {:value (.getAbsolutePath (.getDirectory dir))
                       :doc "Index directory."}
     :max-segment-name {:value (.maxSegmentName status)
                        :doc "The greatest segment name."}
     :missing-segments {:value (.missingSegments status)
                        :doc "True if we were unable to locate and load the segments_N file."}
     :missing-segment-version {:value (.missingSegmentVersion status)
                               :doc "True if we were unable to read the version number from segments_N file."}
     :num-bad-segments {:value (.numBadSegments status)
                        :doc "How many bad segments were found."}
     :num-segments {:value (.numSegments status)
                    :doc "Number of segments in the index."}
     :partial {:value (.partial status)
               :doc "True if we checked only specific segments (CheckIndex.checkIndex(List)) was called with non-null argument)."}
     :segment-format {:value (.segmentFormat status)
                      :doc "String description of the version of the index."}
     :segment-infos {:value (map get-segment-info-details (.segmentInfos status))
                     :doc "List of CheckIndex.Status.SegmentInfoStatus instances, detailing status of each segment."}
     :segments-checked {:value (seq (.segmentsChecked  status))
                        :doc "Empty unless you passed specific segments list to check as optional 3rd argument."}
     :segments-file-name {:value (.segmentsFileName status)
                          :doc "Name of latest segments_N file in the index."}
     :tool-out-of-date {:value (.toolOutOfDate status)
                        :doc "True if the index was created with a newer version of Lucene than the CheckIndex tool."}
     :tot-lose-doc-count {:value (.totLoseDocCount status)
                          :doc "How many documents will be lost to bad segments."}
     :user-data {:value (into {} (.userData status))
                 :doc "Holds the userData of the last commit in the index"}
     :valid-counter {:value (.validCounter status)
                     :doc "Whether the SegmentInfos.counter is greater than any of the segments' names."}}))

(defn check-index-pprint
  [index-dir]
  (clojure.pprint/pprint (check-index index-dir)))

(defn -main
  [index-dir]
  (check-index-pprint index-dir))


(comment
  (check-index-pprint "/home/nipra/lucene-index/pdfs"))
