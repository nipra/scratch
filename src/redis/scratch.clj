(ns redis.scratch
  (:require [taoensso.carmine :as car])
  (:use [redis.init :only [wcar]]))

(comment
  (wcar (car/ping))
  (wcar (car/set "foo" "bar"))
  (wcar (car/get "foo")))

