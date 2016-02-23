(set-env!
  :dependencies '[[org.clojure/clojure                       "1.8.0"  :scope "provided"]
                  [org.clojure/clojurescript                 "1.7.228" :scope "provided"]
                  [adzerk/bootlaces                          "0.1.13" :scope "test"]
                  [adzerk/boot-test                          "1.1.1" :scope "test"]]
  :source-paths #{"test/clj"}
  :resource-paths #{"src/clj" "src/cljc" "src/cljs"}
)

(require
  '[adzerk.boot-test            :refer :all]
  '[adzerk.bootlaces            :refer :all])

(def +version+ "0.0.1")

(bootlaces! +version+ :dont-modify-paths? true)

(task-options!
  pom {:project     'aadurable
       :version     +version+
       :description "Lazy serialization and virtual data structures"
       :url         "https://github.com/aatree/aadurable"
       :scm         {:url "https://github.com/aatree/aadurable"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}}
  aot {:namespace '#{aadurable.proto aadurable.CountedSequence aadurable.nodes}})

(deftask dev
  "Build project for development."
  []
  (comp
    (aot)
;    (show :fileset true)
    (build-jar)
    (target)))

(deftask test-it
   "Setup, compile and run the tests."
   []
   (comp
     (aot)
;     (show :fileset true)
     (run-tests)
     ))

(deftask deploy-release
 "Build for release."
 []
 (comp
   (build-jar)
   (push-release)))
