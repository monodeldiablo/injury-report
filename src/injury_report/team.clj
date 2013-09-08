(ns injury-report.team
  (:require
   [clojure.string :as s]
   [injury-report.core :as c]
   [injury-report.player :as p]
   [net.cgrand.enlive-html :as html]))

(defn mk-team-key
  [profile]
  (map profile [:id :tag]))

(defn mk-profile-url
  [id tag]
  (str "/en" tag "/en/startseite/verein_" id ".html"))

(defn mk-squad-url
  [id tag & [season]]
  (if season
    (str "/en/" tag "/historische-kader/verein_" id "_" season ".html")
    (str "/en/" tag "/startseite/verein_" id ".html")))

(defn short-name
  [page]
  (-> page
      (html/select [:table.tabelle_spieler :h1 :a])
      first
      c/content))

(defn profile
  "Extract a team's profile."
  [url]
  {:id (c/id-from-url url)
   :tag (c/tag-from-url url)
   :name (-> url c/fetch short-name)})

(defn mk-squad-record
  [row]
  (-> row :attrs :href p/mk-key-from-url))

(defn squad
  "Extract the squad list from a provided URL."
  [url]
  (-> url
      c/fetch
      (html/select [:table#spieler :td :a.fb])
      (->> (map mk-squad-record))))
