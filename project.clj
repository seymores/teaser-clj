(defproject seymores/textteaser-clj "0.1.1"
  :description "Summarize urls."
  :url "https://clojars.org/textteaser-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :resource-paths ["resources"]
  :dependencies [[enlive "1.1.5"]
                 [clojure-opennlp "0.3.3"]
                 [io.curtis/boilerpipe-clj "0.3.0"]
                 [org.clojure/clojure "1.6.0"]
                 [http-kit "2.1.18"]
                 [org.clojure/data.priority-map "0.0.5"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :main textteaser-clj.core)
