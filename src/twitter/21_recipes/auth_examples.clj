(ns twitter.21-recipes.auth-examples
  ;; https://github.com/adamwynne/twitter-api
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.restful])
  (:import (twitter.callbacks.protocols SyncSingleCallback))
  (:require [clojure.pprint :as p])
  (:require [useful.config :as config]))

(def ^:dynamic *auth-config* (config/read-config "auth.config"))

(def ^:dynamic *app-consumer-key* (:consumer-key *auth-config*))
(def ^:dynamic *app-consumer-secret* (:consumer-secret *auth-config*))
(def ^:dynamic *user-access-token* (:access-token *auth-config*))
(def ^:dynamic *user-access-token-secret* (:access-token-secret *auth-config*))

(def ^:dynamic *creds*
  (make-oauth-creds *app-consumer-key*
                    *app-consumer-secret*
                    *user-access-token*
                    *user-access-token-secret*))

(comment
  (show-user :oauth-creds *creds* :params {:screen-name "_nipra"})
  (show-friends :params {:screen-name "_nipra"}))
