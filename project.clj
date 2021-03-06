(defproject scratch "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  ;; http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.clojure%22
  ;; http://dev.clojure.org/display/doc/Clojure+Contrib
  ;; http://dev.clojure.org/display/design/Where+Did+Clojure.Contrib+Go
  ;; http://stackoverflow.com/questions/7511789/clojure-lein-how-do-i-include-a-java-file-in-my-project
  ;; http://alexott.net/en/clojure/ClojureLein.html
  :dependencies [[org.clojure/clojure "1.5.1"]

                 [postgresql/postgresql "9.0-801.jdbc4"]
                 [mysql/mysql-connector-java "5.1.18"]
                 [org.xerial/sqlite-jdbc "3.6.16"]
                 [c3p0/c3p0 "0.9.1.2"]
                 [korma "0.3.0-beta9"]

                 ;; https://github.com/kumarshantanu/clj-dbcp
                 ;; Clojure wrapper for Apache DBCP to create JDBC connections pools
                 [clj-dbcp "0.8.0"]

                 [org.apache.lucene/lucene-core "3.6.0"]
                 [org.apache.lucene/lucene-queries "3.6.0"]

                 ;; [org.apache.lucene/lucene-core "4.7.0"]
                 ;; [org.apache.lucene/lucene-analyzers-common "4.7.0"]
                 ;; [org.apache.lucene/lucene-queryparser "4.7.0"]
                 ;; [org.apache.lucene/lucene-queries "4.7.0"]
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

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Redis
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;; Clojure Redis client & message queue
                 ;; https://github.com/ptaoussanis/carmine
                 [com.taoensso/carmine "2.4.6"]
                 
                 ;; redis-clojure is the oldest of several Clojure client libraries for Redis.
                 ;; If you are currently trying to choose which of these client libraries to use,
                 ;; I (tavisrudd) recommend using Carmine instead. It has better documentation,
                 ;; better connection pooling, support for newer features of Redis 2.0+, Leiningen 2.0
                 ;; support, more flexible serialization, and is faster.
                 ;; https://github.com/tavisrudd/redis-clojure
                 ;; [org.clojars.tavisrudd/redis-clojure "1.3.2"]

                 ;; Clojure Redis client library
                 ;; https://github.com/mmcgrana/clj-redis
                 ;; [clj-redis "0.0.12"]

                 ;; A Clojure library for redis
                 ;; https://github.com/abedra/accession
                 ;; [accession "0.1.1" :exclusions [org.clojure/clojure]]

                 ;; Redis client library for clojure
                 ;; https://github.com/wallrat/labs-redis-clojure
                 ;; [labs-redis "0.1.0"]
                 
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;; A tiny library wrapping language-detect that can be used
                 ;; to determine the language of a particular piece of text.
                 ;; https://github.com/dakrone/cld
                 [cld "0.1.0"]

                 ;; Quarzite is a powerful Clojure scheduling library built on
                 ;; top the Quartz Scheduler.
                 [clojurewerkz/quartzite "1.2.0"]

                 [com.rabbitmq/amqp-client "2.7.1"]
                 ;; Langohr is a Clojure wrapper around the RabbitMQ Java client
                 [com.novemberain/langohr "2.3.2"]

                 ;; [org.mongodb/mongo-java-driver "2.10.1"]
                 ;; Monger is an idiomatic Clojure MongoDB driver for a more
                 ;; civilized age.
                 [com.novemberain/monger "1.7.0"]
                 
                 ;; http://neo4j.org/download/
                 ;; [org.neo4j/neo4j "1.7.1"]
                 ;; A Neo4j library for Clojure
                 ;; [clojure-neo4j "0.3.0-SNAPSHOT"]
                 ;; Clojure wrapper for Neo4j, a graph database.
                 ;; https://github.com/wagjo/borneo
                 [borneo "0.4.0"]
                 ;; https://github.com/scusack/neo4j-clj
                 ;; [neo4j-clj "0.0.2-SNAPSHOT"]
                 ;; Neocons, a Clojure client for the Neo4J REST API
                 ;; https://github.com/michaelklishin/neocons
                 [clojurewerkz/neocons "2.0.1"]

                 ;; Welle is an expressive Clojure client for Riak with
                 ;; batteries included.
                 [com.novemberain/welle "2.0.0-beta1"]

                 ;; Clojure OAuth library
                 ;; https://github.com/r0man/oauth-clj
                 ;; [oauth-clj "0.0.5"]
                 ;; OAuth Consumer support for Clojure
                 ;; https://github.com/mattrepl/clj-oauth
                 ;; [clj-oauth "1.2.10"] ; Don't use brings clojure-contrib
                 ;; [clj-oauth "1.3.1-SNAPSHOT"]

                 ;; A simple and flexible library for shelling out in Clojure
                 ;; https://github.com/Raynes/conch
                 [me.raynes/conch "0.5.0"]

                 [javax.mail/mail "1.4.1"]
                 ;; Postal is a library for constructing and sending
                 ;; RFC822-compliant Internet email messages.
                 ;; https://github.com/drewr/postal
                 [com.draines/postal "1.11.1"]
                 ;; Mailer is an ActionMailer-inspired mailer library for Clojure.
                 ;; https://github.com/clojurewerkz/mailer
                 [clojurewerkz/mailer "1.0.0"]

                 ;; A Clojure HTTP library wrapping the Apache
                 ;; HttpComponents client.
                 ;; https://github.com/dakrone/clj-http
                 [clj-http "0.7.9"]
                 ;; http.async.client is based on Asynchronous Http Client
                 ;; for Java.
                 ;; https://github.com/neotyk/http.async.client
                 [http.async.client "0.5.2"]

                 ;; Async io interface to all the twitter APIs
                 ;; https://github.com/adamwynne/twitter-api
                 [twitter-api "0.7.5"]

                 ;; Clojure client for Twitter API
                 ;; https://github.com/mattrepl/clojure-twitter
                 ;; [clojure-twitter "1.2.5"] ; Doesn't work with Clojure 1.3.0
                 [clojure-twitter "1.2.6-SNAPSHOT"]

                 ;; Clojure JSON and JSON SMILE (binary json format)
                 ;; encoding/decoding
                 ;; https://github.com/dakrone/cheshire
                 [cheshire "5.3.1"]
                 
                 ;; Enhanced try and throw for Clojure leveraging Clojure's
                 ;; capabilities 
                 ;; https://github.com/scgilardi/slingshot
                 [slingshot "0.10.3"]

                 ;; A clojure wrapper for Reddit API
                 ;; https://github.com/sunng87/reddit.clj
                 [reddit.clj "0.4.0"]

                 ;; Serialism is A tiny Clojure library that serializes and
                 ;; deserializes values into popular formats based on provided
                 ;; content type.
                 ;; https://github.com/clojurewerkz/serialism
                 [clojurewerkz/serialism "1.0.1"]

                 ;; Latest versions from
                 ;; http://mvnrepository.com/artifact/org.apache.httpcomponents
                 [org.apache.httpcomponents/httpmime "4.1.3"]
                 [org.apache.httpcomponents/httpclient "4.1.3"]
                 [org.apache.httpcomponents/httpcore "4.1.3"]


                 [org.neo4j/neo4j "1.7"]

                 

                 ;; https://github.com/liebke/incanter
                 [incanter "1.5.4"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Web
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 
                 ;; https://github.com/weavejester/compojure/
                 [compojure "1.1.6"]
                 ;; https://github.com/ring-clojure/ring
                 [ring "1.2.1"]

                 ;; https://github.com/ibdknox/noir
                 ;; A framework for writing clojure websites.
                 [noir "1.2.2"]
                 
                 ;; A library heavily inspired by the excellent Ruby library
                 ;; SimpleForm, offering a series of functions for quickly
                 ;; generating forms.
                 ;; https://github.com/asmala/clj-simple-form
                 [clj-simple-form "0.1.0"]

                 ;; Fast library for rendering HTML in Clojure
                 ;; https://github.com/weavejester/hiccup
                 [hiccup "1.0.2"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;;
                 [org.slf4j/slf4j-api "1.6.4"]
                 [org.slf4j/slf4j-log4j12 "1.6.4"]
                 
                 ;; A graph database with pluggable backends, written in Clojure.
                 ;; https://github.com/flatland/jiraph
                 [jiraph "0.8.0-beta6"]
                 
                 ;;;;;;;;;;;;;;;;;;
                 ;; Utilities
                 ;;;;;;;;;;;;;;;;;;
                 
                 ;; A set of clojure utilities created while working on Conjure.
                 ;; https://github.com/macourtney/clojure-tools
                 [clojure-tools "1.1.2"]

                 ;; Some Clojure functions we use all the time, and so can you.
                 ;; https://github.com/flatland/useful
                 [useful "0.8.8"]

                 ;; A support library ClojureWerkz projects (Langohr, Monger,
                 ;; Neocons, Elastisch, Quartzite, Money, etc) can rely on
                 ;; https://github.com/clojurewerkz/support
                 [clojurewerkz/support "0.19.0"]
                 
                 ;; Misclleneous utility functions and macros in Clojure
                 ;; https://github.com/kumarshantanu/clj-miscutil
                 [clj-miscutil "0.4.1"]

                 ;; A library of various small but handy Clojure utility functions
                 ;; https://github.com/mikera/clojure-utils
                 [net.mikera/clojure-utils "0.1.0"]

                 ;; useful functions and extensible macros
                 ;; https://github.com/cgrand/utils
                 [net.cgrand/utils "0.1.0-SNAPSHOT"]

                 ;; Plumbing and Graph: the Clojure utility belt
                 ;; https://github.com/Prismatic/plumbing [Not on clojars]
                 
                 ;; File system utilities for Clojure.
                 ;; https://github.com/Raynes/fs
                 [fs "1.1.2"]

                 ;; Unix-like filesystem manipulation utilities for Clojure,
                 ;; wrapping Apache Commons IO.
                 ;; https://github.com/jashmenn/clj-file-utils
                 [clj-file-utils "0.2.1"]

                 ;; Runa's core utilities. Please use and enjoy.
                 ;; https://github.com/runa-dev/kits
                 [org.clojars.runa/kits "1.5.1"]

                 
                 ;; Others [Not on Clojars, Old, ...]
                 ;; * https://github.com/nathanmarz/storm/blob/master/src/clj/backtype/storm/util.clj [Not a lib]
                 ;; * https://github.com/amitrathore/clj-utils [Old]
                 ;; * https://github.com/arohner/clojure-contrib/tree/1.3-compat
                 ;; * https://github.com/grammati/yoodls [Not on clojars]
                 ;; * https://github.com/jbester/cljext [Old]
                 ;; * https://github.com/overtone/overtone/tree/master/src/overtone/helpers [Not a lib]
                 ;; * https://github.com/pallet/pallet/blob/develop/src/pallet/utils.clj [Not a lib]

                 ;; Java
                 
                 ;; A set of libraries used inside Facebook java projects, internal
                 ;; and open source.
                 ;; https://github.com/facebook/jcommon
                 ;; Latest is 0.1.5. Fail to download.
                 [com.facebook.jcommon/jcommon-all "0.1.3"]
                 
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
                 ;; [weka/weka "3.6.7"]

                 ;; A clojure DSL for system admin and deployment with
                 ;; many remote machines via ssh
                 ;; https://github.com/killme2008/clojure-control
                 [control "0.4.1"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Text Mining
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

                 ;; nipra@unlambda:~/Projects/Clojure/scratch_ss$ mvn install:install-file -Dfile=/home/nipra/Softwares/Java/weka-3-6-7/weka.jar -DartifactId=weka -Dversion=3.6.7 -DgroupId=weka -Dpackaging=jar -DlocalRepositoryPath=maven_repo
                 ;; [weka/weka "3.6.7"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Hadoop/HBase/Ecosystem
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 
                 ;; https://github.com/alexott/clojure-hadoop
                 ;; http://alexott.net/en/clojure/ClojureHadoop.html
                 ;; [clojure-hadoop "1.4.1"]

                 ;; http://mvnrepository.com/artifact/org.apache.hadoop/hadoop-client/2.0.1-alpha
                 ;; [org.apache.hadoop/hadoop-client "2.0.1-alpha"]
                 [org.apache.hadoop/hadoop-client "2.0.0-cdh4.3.0"]
                 
                 ;; [org.apache.hbase/hbase "0.92.1"]
                 [org.apache.hbase/hbase "0.94.6-cdh4.3.0"]
                 ;; https://github.com/davidsantiago/clojure-hbase
                 ;; [clojure-hbase "0.90.5-4"]
                 ;; https://github.com/compasslabs/clojure-hbase-schemas
                 ;; FIXME: [clj-serializer "0.1.1"] -> clojure-contrib
                 ;; [com.compasslabs/clojure-hbase-schemas "0.90.4.4"]

                 ;; https://github.com/urbanairship/statshtable
                 ;; [com.urbanairship/statshtable "1.3.0"]
                 
                 ;; https://github.com/OpenTSDB/asynchbase
                 ;; [org.hbase/asynchbase "1.3.2"]

                 ;; Phoenix: A SQL skin over HBase
                 ;; https://github.com/forcedotcom/phoenix
                 ;; mvn install:install-file -Dfile=~/Softwares/HBase/Phoenix/downloads/phoenix-2.0.0-install/phoenix-2.0.0-client.jar -DartifactId=phoenix -Dversion=2.0.0 -DgroupId=com.salesforce -Dpackaging=jar -DlocalRepositoryPath=maven_repo
                 ;; [com.salesforce/phoenix "1.2.1"]
                 [com.salesforce/phoenix "2.0.0"]
                 [com.google.guava/guava "14.0.1"]

                 ;; Oozie
                 ;; http://archive.cloudera.com/cdh4/cdh/4/oozie-3.3.2-cdh4.3.0/client/apidocs/index.html
                 [org.apache.oozie/oozie-client "3.3.2-cdh4.3.0"]

                 ;; http://archive.cloudera.com/cdh4/cdh/4/oozie-3.3.2-cdh4.3.0/core/apidocs/index.html
                 [org.apache.oozie/oozie-core "3.3.2-cdh4.3.0"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Devops/Sysadmin
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 
                 ;; A clojure DSL for system admin and deployment with
                 ;; many remote machines via ssh
                 ;; https://github.com/killme2008/clojure-control
                 [control "0.4.1"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; JVM/JAVA/System/Instrumentation/Optimization/Profiling
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 
                 ;; Sigmund is friendly clojure wrapper around the Hyperic SIGAR API
                 ;; http://www.hyperic.com/products/sigar. It can tell you all sorts
                 ;; of information about your currently executing process as well as
                 ;; the system that you are working on.
                 ;; https://github.com/zcaudate/sigmund
                 [sigmund "0.1.1"]

                 ;; https://github.com/clojure/java.jmx
                 ;; Doc: http://clojure.github.com/java.jmx/
                 [org.clojure/java.jmx "0.2.0"]

                 ;; https://github.com/clojure/java.classpath
                 ;; Doc: http://clojure.github.com/java.classpath/
                 [org.clojure/java.classpath "0.2.0"]

                 ;; Benchmarking library for clojure
                 ;; https://github.com/hugoduncan/criterium
                 [criterium "0.3.1"]
                 
                 ;; Memory consumption estimator for Java
                 ;; https://github.com/dweiss/java-sizeof
                 [com.carrotsearch/java-sizeof "0.0.3"]

                 ;; Java Agent for Memory Measurements
                 ;; https://github.com/jbellis/jamm
                 [com.github.stephenc/jamm "0.2.5"]
                 
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 ;; Fun
                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                 
                 ;; Collaborative Programmable Music
                 ;; https://github.com/overtone/overtone
                 [overtone "0.8.0"]

                 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                 ;; https://github.com/llasram/inet.data
                 ;; Inet.data is a library for modeling various Internet-related
                 ;; conceptual entities as data, supporting applications which
                 ;; are about the modeled entities versus interfacing with them.
                 [inet.data "0.5.1"]

                 ;; Clojure library for fake data generation, port of ruby faker
                 ;; https://github.com/paraseba/faker
                 [faker "0.2.2"]

                 ;; Thrift
                 ;; http://thrift.apache.org/tutorial/java/
                 ;; http://thrift.apache.org/download/
                 [org.apache.thrift/libthrift "0.9.0"]
                 
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
                 ;; https://github.com/clojure/data.json
                 [org.clojure/data.json "0.2.0"]
                 [org.clojure/data.priority-map "0.0.1"]

                 ;; [org.clojure/data.xml "0.0.3-SNAPSHOT"]
                 
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.1.1"]
                 [org.clojure/math.combinatorics "0.0.2"]
                 [org.clojure/math.numeric-tower "0.0.1"]
                 ;; [org.clojure/test.benchmark "0.1.0-SNAPSHOT"]
                 [org.clojure/test.generative "0.1.4"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/tools.macro "0.1.1"]
                 [org.clojure/tools.namespace "0.2.0"]
                 [org.clojure/tools.nrepl "0.0.5"]
                 [org.clojure/tools.trace "0.7.3"]

                 ;; https://github.com/technomancy/swank-clojure
                 ;; Read section on ``Embedding''
                 [swank-clojure "1.4.3"]

                 [clj-time "0.6.0"]

                 [org.jsoup/jsoup "1.7.3"]]

  ;; For lein < 1.7.0
  ;; :dev-dependencies [[swank-clojure "1.4.0"]
  ;;                    [lein-localrepo "0.3"]
  ;;                    [clj-ns-browser "1.3.0"]]

  ;; for lein >= 1.7.0
  ;; https://github.com/technomancy/swank-clojure/tree/master/lein-swank
  ;; Note: Is not working with lein 2.x.x. New projects are working though.
  ;; Let's stick with lein 1.7.1 for now for this project.
  :plugins [[lein-swank "1.4.5"]]
  
  :repositories {
                 ;; Required for snapshots.
                 "sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"
                 ;; "sonatype-oss-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"
                 ;; "local" ~(str (.toURI (java.io.File. "maven_repo")))
                 "cloudera-repos" "https://repository.cloudera.com/artifactory/cloudera-repos/"
                 "phoenix-github" "https://raw.github.com/forcedotcom/phoenix/maven-artifacts/releases"}
  ;; :resource-paths ["resource"]
  ;; :jvm-opts ["-javaagent:lib/jamm-0.2.5.jar"]
  ;; :aot :all
  :main scratch.main)

