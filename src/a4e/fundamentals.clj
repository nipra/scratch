(ns a4e.fundamentals)

;;; http://en.wikipedia.org/wiki/Greatest_common_divisor
(defn gcd-euclid
  [p q]
  (if (zero? q)
    p
    (let [r (rem p q)]
      (recur q r))))
