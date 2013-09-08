(ns injury-report.player
  (:require
   [clojure.string :as s]
   [injury-report.core :as c]
   [net.cgrand.enlive-html :as html]))

(defn mk-key-from-profile
  [profile]
  (map profile [:id :tag]))

(defn mk-key-from-url
  [url]
  [(c/id-from-url url) (c/tag-from-url url)])

(defn mk-profile-url
  [id tag]
  (str "/en/" tag "/profil/spieler_" id ".html"))

(defn mk-transfer-url
  [id tag]
  (str "/en/" tag "/transfers/spieler_" id ".html"))

(defn from-vcard
  "Given a parsed page and a table key, pull the corresponding
  content."
  [page key]
  (-> page
      (html/select [:table.tabelle_spieler :td])
      (->> (drop-while #(not= key (first (:content %))))
           (second))))

(defn short-name
  [page]
  (-> page
      (html/select [:table.tabelle_spieler :h1])
      first
      c/content
      (s/replace #"^\d+ " "")))

(defn given-name
  [page]
  (-> page
      (from-vcard "Name in native country:")
      c/content
      c/content))

(defn birthdate
  [page]
  (-> page
      (from-vcard "Date of birth:")
      c/content
      c/content
      c/to-iso8601))

(defn birthplace
  [page]
  (-> page
      (from-vcard "Place of birth:")
      c/content
      butlast
      (->> (apply str))))

(defn height
  [page]
  (-> page
      (from-vcard "Height:")
      c/content))

(defn position
  [page]
  (-> page
      (from-vcard "Position:")
      c/content))

(defn foot
  [page]
  (-> page
      (from-vcard "Foot:")
      c/content))

(defn profile
  "Gather a player's vitals."
  [url]
  (let [page (c/fetch url)]
    {:id (c/id-from-url url)
     :tag (c/tag-from-url url)
     :name (short-name page)
     :given-name (or (given-name page) (short-name page))
     :birthdate (birthdate page)
     :birthplace (birthplace page)
     :height (height page)
     :foot (foot page)}))

(defn mk-injury-record
  [id season start end notes]
  {:pid id
   :s (c/to-iso8601 start)
   :e (c/to-iso8601 end)
   :c notes})

(defn- is-injuries-table?
  "The elements aren't labeled in any helpful way, so we've gotta
  blunder around in the dark, looking for some way to distinguish
  injuries from suspensions. Sigh."
  [table]
  (-> table
      (html/select [:th])
      (count)
      (= 4)))

(defn injuries
  "Gather a player's injury history."
  [url]
  (let [id (c/id-from-url url)
        table (->> (html/select (c/fetch url) [:table.standard_tabelle])
                   (filter is-injuries-table?)
                   (take 1))]
    (->> (html/select table [:td])
         (map c/content)
         (partition 4)
         (map #(apply mk-injury-record id %)))))

(defn mk-transfer-record
  [id season date _ from _ to market-value loan? fee _]
  (let [d (-> (or (-> date (html/select [:a]) first)
                  (-> date))
              c/content)]
    ;; HACK: This is necessary because of an obscure typo upstream.
    (when-not (and (= id "110868")
                   (= d "26.01.2013"))
      {:pid id
       :cid (-> to (html/select [:a]) first :attrs :href c/id-from-url)
       :d (c/to-iso8601 d)
       :f (case (c/content fee)
            "?" 0
            "free transfer" 0
            "-" 0
            (-> fee
                c/content
                (s/split #" ")
                first
                (s/replace "." "")
                Integer/parseInt))})))

(defn transfers
  "Gather a player's transfer history."
  [url]
  (let [id (c/id-from-url url)]
    (-> (c/fetch url)
        (html/select [:table.tabelle_grafik])
        last
        (html/select [:tr.hell :td])
        (->> (partition 10)
             (map #(apply mk-transfer-record id %))
             (filter identity)))))
