(ns scratch.core
  (:require [clj-http.client :as http])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clojurewerkz.serialism.core :as s])
  (:require [clojure.java.classpath :as cp])
  (:require [clojure.math.combinatorics :as math])
  (:require [clojure.math.numeric-tower :as num])
  (:require (clojure.tools.namespace [dependency :as ns-dep]
                                     [dir :as ns-dir]
                                     [file :as ns-file]
                                     [find :as ns-find]
                                     [parse :as ns-parse]
                                     [reload :as ns-reload]
                                     [repl :as ns-repl]
                                     [track :as ns-track]))
  (:require (clj-simple-form [form-scope :as form-scope]
                             [input :as form-input]
                             [fields :as form-fields]
                             [helpers :as form-helpers]
                             [util :as form-util]
                             [giddyup :as giddyup]))
  (:require (inet.data [ip :as ip]
                       [dns :as dns]))
  (:require [inet.data.format.psl :as psl])
  (:require (criterium [core :as crit])))

(comment
  (def *img
    (:body (http/get "http://www.gravatar.com/avatar/acb8dccef4e223ce5ba95080f5861457.png"
                     {:as :byte-array})))
  (io/copy *img (io/file "/tmp/nipra.png"))

  (io/copy (:body (http/get "http://www.gravatar.com/avatar/acb8dccef4e223ce5ba95080f5861457.png"
                            {:as :byte-array}))
           (io/file "/tmp/nipra.png"))

  (io/copy (:body (http/get "https://t.co/static/images/bird.png" {:as :byte-array})) (io/file "/tmp/bird.png"))

  (io/copy (:body (http/get "http://bit.ly/V0YspK" {:as :byte-array})) (io/file "/tmp/bird.png")))


(comment
  (def *d (s/serialize "some data" :bytes))
  (String. *d)
  (s/deserialize *d :text)

  (def *d2 (s/serialize "some data" s/octet-stream-content-type))
  (String. *d2)

  (s/serialize {:library "Serialism"} :json)
  (s/serialize {:library "Serialism"} s/json-content-type)
  (s/serialize {:library "Serialism"} s/json-utf8-content-type)

  (s/deserialize "{\"language\":\"Clojure\",\"library\":\"serialism\",\"authors\":[\"Michael\"]}"
                 s/json-content-type))


(comment
  (math/cartesian-product [1 2 3] [4 5 6] [7 8 9]))

(comment
  (def values {:name "Joe" :email "joe@example.com"})
  (def errors {})

  ;; `clj-simple-form.form-scope` handles form bindings
  (giddyup/with-form-scope :profile values errors

    ;; `clj-simple-form.input` provides a set of ready form elements to use
    (form-input/text-field-input :name)
    (form-input/email-field-input :email)

    ;; `clj-simple-form.fields` allows you to use the value hook backend
    ;; with your own inputs
    (form-fields/text-field :coupon-code)

    ;; `clj-simple-form.helpers` includes a few utility functions for forms
    [:div.form-actions
     (form-helpers/cancel-button "/")
     (form-helpers/submit-button)]))


(comment
  (do
    (use 'clj-ns-browser.sdoc)
    (sdoc)))


(comment
  (ip/network-contains? "192.168.1.0/24" "192.168.1.1")
  (ip/address? "600d::")
  (ip/address? "::bad::")

  (let [rfc1918 (ip/network-set "10.0.0.0/8" "172.16.0.0/12" "192.168.0.0/16")]
    [(get rfc1918 "10.31.33.7") ;;=> (#ip/network "10.0.0.0/8")
     (get rfc1918 "8.8.8.8")])  ;;=> nil

  (seq (ip/network "192.168.0.0/30"))
  (ip/network-nth "192.168.0.0/30" -1)
  (ip/address-networks "192.168.0.0" "192.168.0.4")

  (psl/lookup "www.example.co.uk"))
     
    
(comment
  (crit/with-progress-reporting (crit/bench (reduce + (range 100)) :verbose)))

