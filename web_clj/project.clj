(defproject web_clj "0.1.0-SNAPSHOT"
  :plugins [[lein-ring "0.9.6"]]
  :ring {:handler web_clj.core/app
         :nrepl {:start? true
                 :port 3001}}

  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :profiles
  {:dev {:dependencies [[org.clojure/clojure "1.7.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-json "0.2.0"]
                 [compojure "1.4.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 #_[ring-basic-authentication "1.0.2"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]]}})
