(ns utils.solr.nipra.dedup
  (:import [org.apache.solr.update.processor MD5Signature
            Lookup3Signature TextProfileSignature])
  (:import [org.apache.solr.common.params SolrParams ModifiableSolrParams]))

(defmulti get-signature
  (fn [sig-type _ & _]
    sig-type))

(defmethod get-signature :text-profile
  [sig-type content & solr-params]
  (let [text-profile-signature (TextProfileSignature.)]
    (.add text-profile-signature content)
    (String. (.getSignature text-profile-signature))))

(defmethod get-signature :md5
  [sig-type content & solr-params]
  (let [text-profile-signature (MD5Signature.)]
    (.add text-profile-signature content)
    (String. (.getSignature text-profile-signature))))
