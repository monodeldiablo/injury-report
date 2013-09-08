(ns injury-report.stats
  (:require
   [clojure.tools.cli :refer [cli]]
   [clojure.core.memoize :as m]
   [clj-time.core :as j]
   [clj-time.coerce :refer [from-string]]
   [injury-report
    [core :as c]
    [team :as t]
    [player :as p]]))

(defn to-interval
  [start end]
  (j/interval (from-string start)
              (from-string end)))

(defn overlap
  [reference record]
  (when-let [period (.overlap reference (:period record))]
    (assoc record :period period)))

(defn injury-record-to-interval
  [record]
  {:pid (:pid record)
   :c (:c record)
   :period (to-interval (:s record) (:e record))})

(defn injury-intervals
  [records]
  (map injury-record-to-interval records))

(defn transfer-record-to-interval
  [from & [to]]
  (let [to (or (:d to)
               (j/today))]
    {:pid (:pid from)
     :cid (:cid from)
     :f (:f from)
     :period (to-interval (:d from) to)}))

(defn transfer-intervals
  [records]
  (let [latest (first records)]
    (->> records
         (partition 2 1)
         (map (fn [[to from]] (transfer-record-to-interval from to)))
         (cons (transfer-record-to-interval latest)))))

;; memoize any scraping
(def get-squad*
  (m/memo t/squad))
(def get-injuries*
  (m/memo p/injuries))
(def get-transfers*
  (m/memo p/transfers))

(def injuries (atom {}))
(def players (atom {}))

(def seasons
  {"2012" ["2012-08-18" "2013-05-19"]
   "2011" ["2011-08-13" "2012-05-13"]
   "2010" ["2010-08-14" "2011-05-22"]
   "2009" ["2009-08-15" "2010-05-09"]
   "2008" ["2008-08-16" "2009-05-24"]
   "2007" ["2007-08-11" "2008-05-11"]})

;; slurp up the squad list for each season
(defn get-players
  [id tag season]
  (let [p (-> (t/mk-squad-url id tag season)
              (get-squad*))]
    (swap! players assoc season p)
    p))

;; slurp each player's injury history
(defn get-injuries
  "Grab a given player's injury history for this club during this time period."
  [player club reference]
  (let [[id tag] player
        [cid ctag] club
        transfers (->> (p/mk-transfer-url id tag)
                       (get-transfers*)
                       (transfer-intervals)
                       (filter #(= cid (:cid %)))
                       (map #(overlap reference %))
                       (filter identity))
        injs (->> (p/mk-profile-url id tag)
                  (get-injuries*)
                  (injury-intervals))]
    (->> (for [t transfers
               i injs]
           (overlap (:period t) i))
         (filter identity))))

;; - club
;; - season
;; - number of athletes
;; - number of injuries
;; - athletes affected by injury
;; - mean injuries per player
;; - median injuries per player
;; - total days lost to injury
;; - mean days lost to injury (per player)
;; - median days lost to injury
;; - injury type histogram

(defn generate-player-stats
  [[p inj]]
  (let [days (map #(j/in-days (:period %)) inj)]
    [p {:injuries (count inj)
        :durations days
        :total-days (reduce + days)}]))

(defn generate-season-stats
  [[s ps]]
  (let [num-players-injured (count ps)
        per-player-stats (->> (map generate-player-stats ps)
                              (into {}))
        _ (prn per-player-stats)
        total-injuries (reduce (fn [sum [p stats]]
                                 (+ sum (:injuries stats)))
                               0
                               per-player-stats)
        total-days (reduce (fn [sum [p stats]]
                             (+ sum (:total-days stats)))
                           0
                           per-player-stats)]
    [s {:injured-players num-players-injured
             :injuries total-injuries
             :days total-days
             :total-players (count (@players s))}]))

(defn generate-stats
  "Provided a mapping of injuries in the form {<season> {<player>
  <injuries seq>}...}, generate a statistical breakdown by season."
  [inj]
  (->> (map generate-season-stats inj)
       (into {})))


(defn -main
  [& args]
  (let [[options _ banner]
        (cli args
             ["--[no-]help" "Show help" :flag true]
             ["--tag" "club tag"]
             ["--id" "club id"]
             ["--season" "season"])
        id (:id options)
        tag (:tag options)]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (doseq [s (keys seasons)]
      (doseq [;s (keys seasons)
              player (get-players (:id options) (:tag options) s)
              :let [add-injuries (fn [injs]
                                   (when-not (empty? injs)
                                     (swap! injuries
                                            assoc-in
                                            [s player]
                                            injs)))]]
        (println "fetching" player "for" tag "during" s)
        (->> (seasons s)
             (apply to-interval)
             (get-injuries player [id tag])
             (add-injuries)
             (doall)))
      (->> (generate-stats @injuries)
           (prn)
           (println)
           (doall)))))
