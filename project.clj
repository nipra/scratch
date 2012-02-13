(defproject scratch "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  ;; http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.clojure%22
  ;; http://dev.clojure.org/display/doc/Clojure+Contrib
  ;; http://dev.clojure.org/display/design/Where+Did+Clojure.Contrib+Go
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.mongodb/mongo-java-driver "2.6.3"]
                 [postgresql/postgresql "9.0-801.jdbc4"]
                 [mysql/mysql-connector-java "5.1.18"]
                 [org.apache.lucene/lucene-core "3.4.0"]
                 [org.apache.lucene/lucene-queries "3.4.0"]
                 [javax.mail/mail "1.4.1"]
                 [org.apache.solr/solr-core "3.4.0"]
                 [org.apache.solr/solr-solrj "3.4.0"]
                 [com.rabbitmq/amqp-client "2.7.1"]
                 [com.indextank/indextank-java "1.0.9"]

                 [org.clojure/algo.generic "0.1.1-SNAPSHOT"]
                 [org.clojure/algo.monads "0.1.3-SNAPSHOT"]

                 ;; [org.clojure/build.poms "0.0.26-SNAPSHOT"]
                 ;; [org.clojure/pom.contrib "0.0.25"]
                 
                 [org.clojure/core.cache "0.5.0"]
                 [org.clojure/core.incubator "0.1.1-SNAPSHOT"]
                 [org.clojure/core.logic "0.6.8-SNAPSHOT"]
                 [org.clojure/core.match "0.2.0-alpha9"]
                 [org.clojure/core.memoize "0.5.1"]
                 [org.clojure/core.unify "0.5.2"]
                 [org.clojure/data.codec "0.1.0"]
                 [org.clojure/data.csv "0.1.0"]
                 [org.clojure/data.finger-tree "0.0.1"]
                 [org.clojure/data.json "0.1.2"]
                 [org.clojure/data.priority-map "0.0.2-SNAPSHOT"]

                 ;; [org.clojure/data.xml "0.0.3-SNAPSHOT"]
                 
                 [org.clojure/data.zip "0.1.1-SNAPSHOT"]
                 [org.clojure/java.classpath "0.2.0"]
                 [org.clojure/java.data "0.0.1-SNAPSHOT"]
                 [org.clojure/java.jdbc "0.1.1"]
                 [org.clojure/java.jmx "0.2-SNAPSHOT"]
                 [org.clojure/math.combinatorics "0.0.3-SNAPSHOT"]
                 [org.clojure/math.numeric-tower "0.0.2-SNAPSHOT"]
                 ;; [org.clojure/test.benchmark "0.1.0-SNAPSHOT"]
                 [org.clojure/test.generative "0.1.5-SNAPSHOT"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.clojure/tools.logging "0.2.4-SNAPSHOT"]
                 [org.clojure/tools.macro "0.1.2-SNAPSHOT"]
                 [org.clojure/tools.namespace "0.1.2"]
                 [org.clojure/tools.nrepl "0.0.5"]
                 [org.clojure/tools.trace "0.7.2-SNAPSHOT"]
                 ]

  :dev-dependencies [[swank-clojure "1.4.0"]]
  :repositories {
                 ;; Required for snapshots.
                 "sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"
                 ;; "sonatype-oss-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"
                 })

