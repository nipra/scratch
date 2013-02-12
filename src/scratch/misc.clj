(ns scratch.misc
  (:require (clj-time [core :as ctc]
                      [format :as ctf]
                      [coerce :as ctco]
                      [local :as ctl])))

(defn stock-value-per-year
  [current-date vest-date unvested-value]
  (let [formatter (ctf/formatter "dd-MM-yyyy")
        current-date-time (ctf/parse formatter current-date)
        vest-date-time (ctf/parse formatter vest-date)
        interval (ctc/interval current-date-time vest-date-time)
        days (ctc/in-days interval)]
    (* (/ unvested-value days)
       365)))

(defn value-per-year
  [number-of-shares sold vest-date usd-in-inr stock-price]
  (let [formatter (ctf/formatter "dd-MM-yyyy")
        vest-date-time (ctf/parse formatter vest-date)
        days (ctc/in-days (ctc/interval (ctc/now) vest-date-time))
        remaining-shares (- number-of-shares sold)
        net-value-in-usd (- (* remaining-shares stock-price)
                            (* remaining-shares 6.8))
        net-value-in-inr (* net-value-in-usd usd-in-inr)]
    (* (/ net-value-in-inr days) 365)))
