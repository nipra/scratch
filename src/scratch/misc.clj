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
