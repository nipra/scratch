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
                                           RegexStringComparator)
           (org.apache.hadoop.hbase.util Bytes)))

(defn row-filter-with-regex
  [regex]
  (RowFilter. CompareFilter$CompareOp/EQUAL (RegexStringComparator. regex)))


(defn column-filter
  [column-family qualifier]
  (let [qualifier-filter (QualifierFilter. CompareFilter$CompareOp/EQUAL
                                           (BinaryComparator. (Bytes/toBytes qualifier)))
        family-filter (FamilyFilter. CompareFilter$CompareOp/EQUAL
                                     (BinaryComparator. (Bytes/toBytes column-family)))]
    (FilterList. [qualifier-filter family-filter])))


(defn qualifier-filter
  [qualifier]
  (QualifierFilter. CompareFilter$CompareOp/EQUAL (BinaryComparator. (Bytes/toBytes qualifier))))


(defn family-filter
  [family]
  (FamilyFilter. CompareFilter$CompareOp/EQUAL (BinaryComparator. (Bytes/toBytes family))))

