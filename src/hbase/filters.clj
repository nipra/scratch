(ns hbase.filters
  (:import (org.apache.hadoop.hbase.filter ValueFilter
                                           CompareFilter
                                           CompareFilter$CompareOp
                                           WritableByteArrayComparable
                                           BinaryComparator
                                           SingleColumnValueFilter
                                           PrefixFilter
                                           QualifierFilter
                                           FamilyFilter
                                           FilterList
                                           RowFilter
                                           RegexStringComparator
                                           ColumnRangeFilter
                                           ColumnPrefixFilter)
           (org.apache.hadoop.hbase.util Bytes)))

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
