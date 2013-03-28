(ns utils.date-time
  (:import (org.joda.time Period Seconds DateTime))
  (:import (org.joda.time.format PeriodFormatterBuilder PeriodFormatter))
  (:require (clj-time [core :as ctc]
                      [format :as ctf]
                      [coerce :as ctco]
                      [local :as ctl])))

;;; http://joda-time.sourceforge.net/api-release/index.html?org/joda/time/format/ISODateTimeFormat.html
(def yyyyMMdd (ctf/formatter "yyyyMMdd"))

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

(defn yyyymmdd->millis
  "`date' in yyyyMMdd format."
  [date]
  (ctco/to-long (ctf/parse yyyyMMdd date)))

(comment
  ;; Examples
  (ctc/days 1)
  (ctc/plus (ctc/now) (ctc/days 1))
  (ctco/to-long (ctf/parse (:basic-date-time-no-ms ctf/formatters) "20120101T000000Z"))
  (ctco/to-date (ctco/to-long (ctf/parse (:basic-date ctf/formatters) "20120301")))
  (ctco/to-date-time (ctco/to-long (ctf/parse (:basic-date ctf/formatters) "20120301")))
  (ctco/to-long (ctl/to-local-date-time (ctf/parse utils.date-time/yyyyMMdd "20120301")))
  (ctf/unparse (:basic-date ctf/formatters)
               (ctf/parse (:basic-date ctf/formatters) "20120301")))
