(defproject scratch "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  ;; http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.clojure%22
  ;; http://dev.clojure.org/display/doc/Clojure+Contrib
  ;; http://dev.clojure.org/display/design/Where+Did+Clojure.Contrib+Go
  ;; http://stackoverflow.com/questions/7511789/clojure-lein-how-do-i-include-a-java-file-in-my-project
  ;; http://alexott.net/en/clojure/ClojureLein.html
  :dependencies [[org.clojure/clojure "1.3.0"]

                 [postgresql/postgresql "9.0-801.jdbc4"]
                 [mysql/mysql-connector-java "5.1.18"]
                 [org.xerial/sqlite-jdbc "3.6.16"]
                 ;; https://github.com/kumarshantanu/clj-dbcp
                 ;; Clojure wrapper for Apache DBCP to create JDBC connections pools
                 [clj-dbcp "0.8.0"]

                 [org.apache.lucene/lucene-core "3.6.0"]
                 [org.apache.lucene/lucene-queries "3.6.0"]
                 [clucy "0.3.0"]

                 ;; Core interfaces/classes, external parser libs, GUI, CLI
                 ;; Issues with slf4j versions because of one of the parsers included
                 ;; in tika-app. Use tika-parsers instead.
                 ;; [org.apache.tika/tika-app "1.1"]
                 [org.apache.tika/tika-parsers "1.1"]

                 [org.apache.solr/solr-core "3.6.0"]
                 [org.apache.solr/solr-solrj "3.6.0"]

                 [com.indextank/indextank-java "1.0.9"]

                 ;; http://jsr-108.sourceforge.net/javadoc/index.html
                 ;; [javax.units/jsr108 "0.01"] 

                 [org.clojars.tavisrudd/redis-clojure "1.3.1"]

                 ;; A tiny library wrapping language-detect that can be used
                 ;; to determine the language of a particular piece of text.
                 ;; https://github.com/dakrone/cld
                 [cld "0.1.0"]

                 ;; Quarzite is a powerful Clojure scheduling library built on
                 ;; top the Quartz Scheduler.
                 [clojurewerkz/quartzite "1.0.0-rc5"]

                 [com.rabbitmq/amqp-client "2.7.1"]
                 ;; Langohr is a Clojure wrapper around the RabbitMQ Java client
                 [com.novemberain/langohr "1.0.0-beta2"]

                 ;; [org.mongodb/mongo-java-driver "2.6.3"]
                 ;; Monger is an idiomatic Clojure MongoDB driver for a more
                 ;; civilized age.
                 [com.novemberain/monger "1.0.0-beta8"]
                 
                 ;; http://neo4j.org/download/
                 [org.neo4j/neo4j "1.7.1"]
                 ;; A Neo4j library for Clojure
                 [clojure-neo4j "0.3.0-SNAPSHOT"]
                 ;; Clojure wrapper for Neo4j, a graph database.
                 ;; https://github.com/wagjo/borneo
                 [borneo "0.3.0"]
                 ;; https://github.com/scusack/neo4j-clj
                 [neo4j-clj "0.0.2-SNAPSHOT"]
                 ;; Neocons, a Clojure client for the Neo4J REST API
                 ;; https://github.com/michaelklishin/neocons
                 [clojurewerkz/neocons "1.0.0"]

                 ;; Welle is an expressive Clojure client for Riak with
                 ;; batteries included.
                 [com.novemberain/welle "1.1.0"]

                 [org.apache.hbase/hbase "0.92.1"]
                 ;; https://github.com/davidsantiago/clojure-hbase
                 ;; [clojure-hbase "0.90.5-4"]
                 ;; https://github.com/compasslabs/clojure-hbase-schemas
                 ;; FIXME: [clj-serializer "0.1.1"] -> clojure-contrib
                 ;; [com.compasslabs/clojure-hbase-schemas "0.90.4.4"]

                 ;; Clojure OAuth library
                 ;; https://github.com/r0man/oauth-clj
                 [oauth-clj "0.0.5"]
                 ;; OAuth Consumer support for Clojure
                 ;; https://github.com/mattrepl/clj-oauth
                 ;; [clj-oauth "1.2.10"] ; Don't use brings clojure-contrib
                 [clj-oauth "1.3.1-SNAPSHOT"]

                 ;; A simple and flexible library for shelling out in Clojure
                 ;; https://github.com/Raynes/conch
                 [conch "0.2.1"]

                 [javax.mail/mail "1.4.1"]
                 ;; Postal is a library for constructing and sending
                 ;; RFC822-compliant Internet email messages.
                 ;; https://github.com/drewr/postal
                 [com.draines/postal "1.8.0"]
                 ;; Mailer is an ActionMailer-inspired mailer library for Clojure.
                 ;; https://github.com/clojurewerkz/mailer
                 [clojurewerkz/mailer "1.0.0-alpha3"]

                 ;; A Clojure HTTP library wrapping the Apache
                 ;; HttpComponents client.
                 ;; https://github.com/dakrone/clj-http
                 [clj-http "0.4.2"]
                 ;; http.async.client is based on Asynchronous Http Client
                 ;; for Java.
                 ;; https://github.com/neotyk/http.async.client
                 [http.async.client "0.4.5"]

                 ;; Async io interface to all the twitter APIs
                 ;; https://github.com/adamwynne/twitter-api
                 [twitter-api "0.6.10"]

                 ;; Clojure client for Twitter API
                 ;; https://github.com/mattrepl/clojure-twitter
                 ;; [clojure-twitter "1.2.5"] ; Doesn't work with Clojure 1.3.0
                 [clojure-twitter "1.2.6-SNAPSHOT"]

                 ;; Clojure JSON and JSON SMILE (binary json format)
                 ;; encoding/decoding
                 ;; https://github.com/dakrone/cheshire
                 [cheshire "4.0.0"]

                 ;; Enhanced try and throw for Clojure leveraging Clojure's
                 ;; capabilities 
                 ;; https://github.com/scgilardi/slingshot
                 [slingshot "0.10.2"]

                 ;; A clojure wrapper for Reddit API
                 ;; https://github.com/sunng87/reddit.clj
                 [reddit.clj "0.3.3"]

                 ;; Latest versions from
                 ;; http://mvnrepository.com/artifact/org.apache.httpcomponents
                 [org.apache.httpcomponents/httpmime "4.1.3"]
                 [org.apache.httpcomponents/httpclient "4.1.3"]
                 [org.apache.httpcomponents/httpcore "4.1.3"]

                 [korma "0.3.0-beta9"]
                 [org.neo4j/neo4j "1.7"]

                 [clj-time "0.4.2"]

                 ;; https://github.com/liebke/incanter
                 [incanter "1.3.0"]

                 ;; https://github.com/weavejester/compojure/
                 [compojure "1.1.1"]
                 ;; https://github.com/ring-clojure/ring
                 [ring "1.1.1"]

                 ;; https://github.com/ibdknox/noir
                 ;; A framework for writing clojure websites.
                 [noir "1.2.2"]

                 ;;
                 [org.slf4j/slf4j-api "1.6.4"]
                 [org.slf4j/slf4j-log4j12 "1.6.4"]
                 
                 ;; https://github.com/Raynes/fs
                 ;; File system utilities for Clojure.
                 [fs "1.1.2"]

                 ;; https://github.com/jashmenn/clj-file-utils
                 ;; Unix-like filesystem manipulation utilities for Clojure,
                 ;; wrapping Apache Commons IO.
                 [clj-file-utils "0.2.1"]

                 ;; A graph database with pluggable backends, written in Clojure.
                 ;; https://github.com/flatland/jiraph
                 [jiraph "0.8.0-beta6"]
                 
                 ;;;;;;;;;;;;;;;;;;
                 ;; Utilities
                 ;;;;;;;;;;;;;;;;;;
                 
                 ;; A set of clojure utilities created while working on Conjure.
                 ;; https://github.com/macourtney/clojure-tools
                 [clojure-tools "1.1.1"]

                 ;; Some Clojure functions we use all the time, and so can you.
                 ;; https://github.com/flatland/useful
                 [useful "0.8.3-alpha7"]

                 ;; A support library ClojureWerkz projects (Langohr, Monger,
                 ;; Neocons, Elastisch, Quartzite, Money, etc) can rely on
                 ;; https://github.com/clojurewerkz/support
                 [clojurewerkz/support "0.6.0"]

                 ;; Clojure HTTP library using the Apache HttpClient.
                 ;; https://github.com/rnewman/clj-apache-http
                 ;; [com.twinql.clojure/clj-apache-http "2.3.1"]
                 ;; Use https://github.com/tavisrudd/clj-apache-http
                 ;; Doesn't depend on clojure-contrib
                 [org.clojars.tavisrudd/clj-apache-http "2.3.2-SNAPSHOT"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Data Mining/Machine Learning
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 
                 ;; nipra@unlambda:~/Projects/Clojure/scratch_ss$ mvn install:install-file -Dfile=/home/nipra/Softwares/Java/weka-3-6-7/weka.jar -DartifactId=weka -Dversion=3.6.7 -DgroupId=weka -Dpackaging=jar -DlocalRepositoryPath=maven_repo
                 ;; https://github.com/antoniogarrote/clj-ml
                 [weka/weka "3.6.7"]

                 ;; A clojure DSL for system admin and deployment with
                 ;; many remote machines via ssh
                 ;; https://github.com/killme2008/clojure-control
                 [control "0.4.1"]
                 
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Text Mining
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;; http://gate.ac.uk/
                 ;; http://mvnrepository.com/artifact/uk.ac.gate
                 ;; open source software capable of solving almost any
                 ;; text processing problem
                 [uk.ac.gate/gate-core "7.0"]

                 ;; http://opennlp.apache.org/index.html
                 ;; http://opennlp.apache.org/maven-dependency.html
                 ;; http://mvnrepository.com/artifact/org.apache.opennlp
                 ;; The Apache OpenNLP library is a machine learning based
                 ;; toolkit for the processing of natural language text.
                 [org.apache.opennlp/opennlp-tools "1.5.2-incubating"]
                 [org.apache.opennlp/opennlp-uima "1.5.2-incubating"]
                 [org.apache.opennlp/opennlp-maxent "3.0.2-incubating"]
                 ;; Clojure library interface to OpenNLP
                 [clojure-opennlp "0.1.10"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Development/Productivity/Tools
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;; https://github.com/dakrone/clojuredocs-client
                 ;; A client for the http://clojuredocs.org API
                 [org.thnetos/cd-client "0.3.4"]

                 ;; https://github.com/ninjudd/clojure-complete
                 ;; Clojure-complete is an symbol completion library for Clojure.
                 ;; The code is adapted from jochu/swank-clojure.
                 [clojure-complete "0.2.1"]
                 
                 ;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Clojure core libs
                 ;;;;;;;;;;;;;;;;;;;;;;;
                 
                 [org.clojure/algo.generic "0.1.0"]
                 [org.clojure/algo.monads "0.1.0"]

                 ;; [org.clojure/build.poms "0.0.26-SNAPSHOT"]
                 ;; [org.clojure/pom.contrib "0.0.25"]
                 
                 [org.clojure/core.cache "0.5.0"]
                 [org.clojure/core.incubator "0.1.0"]
                 [org.clojure/core.logic "0.7.4"]
                 [org.clojure/core.match "0.2.0-alpha9"]
                 [org.clojure/core.memoize "0.5.1"]
                 [org.clojure/core.unify "0.5.2"]
                 [org.clojure/data.codec "0.1.0"]
                 [org.clojure/data.csv "0.1.0"]
                 [org.clojure/data.finger-tree "0.0.1"]
                 [org.clojure/data.json "0.1.2"]
                 [org.clojure/data.priority-map "0.0.1"]

                 ;; [org.clojure/data.xml "0.0.3-SNAPSHOT"]
                 
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/java.classpath "0.2.0"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.1.1"]
                 [org.clojure/java.jmx "0.2.0"]
                 [org.clojure/math.combinatorics "0.0.2"]
                 [org.clojure/math.numeric-tower "0.0.1"]
                 ;; [org.clojure/test.benchmark "0.1.0-SNAPSHOT"]
                 [org.clojure/test.generative "0.1.4"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/tools.macro "0.1.1"]
                 [org.clojure/tools.namespace "0.1.2"]
                 [org.clojure/tools.nrepl "0.0.5"]
                 [org.clojure/tools.trace "0.7.3"]]

  :dev-dependencies [[swank-clojure "1.4.0"]]
  :repositories {
                 ;; Required for snapshots.
                 "sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"
                 ;; "sonatype-oss-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"
                 "local" ~(str (.toURI (java.io.File. "maven_repo")))
                 }
  ;; :resource-paths ["resource"]
  )


