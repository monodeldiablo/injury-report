(defproject injury-report "0.1.0-SNAPSHOT"
  :description "Scrape injury data from TransferMarkt"
  :url "http://injuryreport.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"]
                 [org.clojure/core.memoize "0.5.6"]
                 [clj-http "0.7.6"]
                 [clj-time "0.6.0"]
                 [enlive "1.1.4"]
                 ;[apage43/cbdrawer "0.2.1"]
                 ]
  :main injury-report.stats)
