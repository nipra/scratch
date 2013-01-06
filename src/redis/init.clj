(ns redis.init
  (:require [taoensso.carmine :as car]))

(def pool (car/make-conn-pool))
(def spec-server (car/make-conn-spec))

(defmacro wcar [& body]
  `(car/with-conn pool spec-server ~@body))
