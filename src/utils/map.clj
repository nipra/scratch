(ns utils.map)

;; https://groups.google.com/d/msg/clojure/gGigc9BWzNw/9h-kHrgmND4J
(defn select-in [m keyseq]
  (loop [acc {} [k & ks] (seq keyseq)]
    (if k
      (recur
       (if (sequential? k)
         (let [[k ks] k]
           (assoc acc k
                  (select-in (get m k) ks)))
         (assoc acc k (get m k)))
       ks)
      acc))) 

(comment
  (def my-map {:name "John Doe"
               :email "john@doe.com"
               :address {:house "42"
                         :street "Moon St."
                         :city "San Francisco"
                         :state "CA"
                         :zip 76509
                         :mobile "+188888888"}
               :ssn "123456"
               :spouse {:name "Jane Doe"
                        :ssn "654321"
                        :relation :wife
                        :address {:house "42"
                                  :street "Moon St."
                                  :city "Atlanta"
                                  :state "GA"
                                  :zip 76509
                                  :mobile "+188888888"}}})
  (select-in my-map [:name
                     :email
                     [:address [:city
                                :state]]
                     [:spouse [:name
                               [:address [:city
                                          :state]]]]]) )
