(defproject scratch "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  ;; http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.clojure%22
  ;; http://dev.clojure.org/display/doc/Clojure+Contrib
  ;; http://dev.clojure.org/display/design/Where+Did+Clojure.Contrib+Go
  ;; http://stackoverflow.com/questions/7511789/clojure-lein-how-do-i-include-a-java-file-in-my-project
  ;; http://alexott.net/en/clojure/ClojureLein.html
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.mongodb/mongo-java-driver "2.6.3"]
                 [postgresql/postgresql "9.0-801.jdbc4"]
                 [mysql/mysql-connector-java "5.1.18"]

                 [org.apache.lucene/lucene-core "3.6.0"]
                 [org.apache.lucene/lucene-queries "3.6.0"]
                 [clucy "0.3.0"]
                 ;; Core interfaces/classes, external parser libs, GUI, CLI
                 [org.apache.tika/tika-app "1.1"]

                 [org.apache.solr/solr-core "3.6.0"]
                 [org.apache.solr/solr-solrj "3.6.0"]
                 
                 [javax.mail/mail "1.4.1"]
                 [com.rabbitmq/amqp-client "2.7.1"]
                 [com.indextank/indextank-java "1.0.9"]
                 ;; [javax.units/jsr108 "0.01"] ; http://jsr-108.sourceforge.net/javadoc/index.html

                 [org.clojars.tavisrudd/redis-clojure "1.3.1"]

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
                 [org.clojure/tools.trace "0.7.3"]
                 ]

  :dev-dependencies [[swank-clojure "1.4.0"]]
  :repositories {
                 ;; Required for snapshots.
                 "sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"
                 ;; "sonatype-oss-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"
                 })

