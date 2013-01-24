(ns hbase.filters
  (:import (org.apache.hadoop.hbase.filter ValueFilter
                                           CompareFilter
                                           CompareFilter$CompareOp
                                           SingleColumnValueFilter
                                           PrefixFilter
                                           QualifierFilter
                                           FamilyFilter
                                           FilterList
                                           RowFilter
                                           RegexStringComparator
                                           ColumnRangeFilter
                                           ColumnPrefixFilter)
           (org.apache.hadoop.hbase.filter WritableByteArrayComparable
                                           BinaryComparator
                                           BinaryPrefixComparator
                                           BitComparator
                                           BitComparator$BitwiseOp)
           (org.apache.hadoop.hbase.util Bytes)))

(def eq CompareFilter$CompareOp/EQUAL)
(def gt CompareFilter$CompareOp/GREATER)
(def gte CompareFilter$CompareOp/GREATER_OR_EQUAL)
(def lt CompareFilter$CompareOp/LESS)
(def lte CompareFilter$CompareOp/LESS_OR_EQUAL)
(def ne CompareFilter$CompareOp/NOT_EQUAL)

(declare make-bit-comparator)
(def binary-and (make-bit-comparator BitComparator$BitwiseOp/AND))
(def binary-or (make-bit-comparator BitComparator$BitwiseOp/OR))
(def binary-xor (make-bit-comparator BitComparator$BitwiseOp/XOR))

(defn binary-comparator
  [value]
  (BinaryComparator. (Bytes/toBytes value)))

(defn binary-prefix-comparator
  [value]
  (BinaryPrefixComparator. (Bytes/toBytes value)))

(defn bit-comparator
  [value bitwise-op]
  (BitComparator. (Bytes/toBytes value) bitwise-op))

(defn make-bit-comparator
  [bitwise-op]
  (fn [value]
    (bit-comparator value bitwise-op)))

(defn column-filter
  [column-family qualifier]
  (let [qualifier-filter (QualifierFilter. CompareFilter$CompareOp/EQUAL
                                           (BinaryComparator. (Bytes/toBytes qualifier)))
        family-filter (FamilyFilter. CompareFilter$CompareOp/EQUAL
                                     (BinaryComparator. (Bytes/toBytes column-family)))]
    (FilterList. [qualifier-filter family-filter])))


(defn column-qualifier-filter
  [qualifier]
  (QualifierFilter. CompareFilter$CompareOp/EQUAL (BinaryComparator. (Bytes/toBytes qualifier))))


(defn column-family-filter
  [family]
  (FamilyFilter. CompareFilter$CompareOp/EQUAL (BinaryComparator. (Bytes/toBytes family))))

;;; Column filters
(defn column-range-filter
  [min-column max-column]
  (ColumnRangeFilter. (Bytes/toBytes min-column) true
                      (Bytes/toBytes max-column) true))

(defn column-prefix-filter
  [prefix]
  (ColumnPrefixFilter. (Bytes/toBytes prefix)))

;;; Row filters
(defn row-filter-with-regex
  [regex]
  (RowFilter. CompareFilter$CompareOp/EQUAL (RegexStringComparator. regex)))

(defn row-prefix-filter
  [prefix]
  (PrefixFilter. (Bytes/toBytes prefix)))

;;; Value filters
(defn value-filter
  [value & {:keys [compare-op comparator-fn]
            :or {compare-op eq
                 comparator-fn binary-comparator}}]
  (ValueFilter. compare-op (comparator-fn value)))

(defn column-value-filter
  "NOTE: When using this filter on a Scan with specified inputs, the column to 
   be tested should also be added as input (otherwise the filter will regard the
   column as missing)."
  [column-family qualifier value & {:keys [compare-op comparator-fn]
                                    :or {compare-op eq
                                         comparator-fn binary-comparator}}]
  (SingleColumnValueFilter. (Bytes/toBytes column-family)
                            (Bytes/toBytes qualifier)
                            compare-op
                            comparator-fn))
