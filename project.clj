(defproject edin "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"] [org.clojure/clojurescript "0.0-2371"]]
  :main ^:skip-aot edin.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild { 
               :builds [{
                         :source-paths ["src-cljs"]
                         :compiler {
                                    :output-to "dist/js/main.js"
                                    :optimizations :whitespace
                                    :pretty-print true
                                    }
                         }]
               } 
  )
