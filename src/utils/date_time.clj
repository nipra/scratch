(ns utils.date-time
  (:import (org.joda.time Period Seconds))
  (:import (org.joda.time.format PeriodFormatterBuilder PeriodFormatter))
  (:require (clj-time [core :as ctc]
                      [format :as ctf]
                      [coerce :as ctco]
                      [local :as ctl])))

;;; FIXME: Incomplete
(defn readable-secs
  [secs]
  (let [period (.toPeriod (Seconds/seconds secs))
        period-formatter (.toFormatter (doto (PeriodFormatterBuilder.)
                                         (.appendDays)
                                         (.appendSuffix " day" "days")))]
    (with-out-str (println (.print period-formatter period)))))

(defn yyyymmdd-range*
  [start stop]
  (let [formatter (ctf/formatter "yyyyMMdd")
        start-date (ctf/parse formatter start)
        stop-date (ctf/parse formatter stop)]
    (loop [dates [start]
           prev-date start-date
           next-date (ctc/plus prev-date (ctc/days 1))]
      (if (ctc/within? (ctc/interval start-date stop-date) next-date)
        (recur (conj dates (ctf/unparse formatter next-date))
               next-date
               (ctc/plus next-date (ctc/days 1)))
        (conj dates stop)))))

(defn yyyymmdd-range
  [start stop]
  (let [formatter (ctf/formatter "yyyyMMdd")
        start-date (ctf/parse formatter start)
        stop-date (ctf/parse formatter stop)
        date-range (iterate #(ctc/plus % (ctc/days 1)) start-date)
        within? (partial ctc/within? (ctc/interval start-date stop-date))
        dates (take-while within? date-range)
        format-date (partial ctf/unparse formatter)]
    (conj (vec (map format-date dates)) stop)))
