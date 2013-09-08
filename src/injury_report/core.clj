(ns injury-report.core
  (:require
   [clojure.string :as s]
   [clj-http.client :as client]
   [net.cgrand.enlive-html :as html]))

(def http-agent "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.57 Safari/537.36")

(def server "http://www.transfermarkt.com")

(defn fetch [path]
  (-> (str server path)
      (client/get {:headers {"User-Agent" http-agent}})
      :body
      java.io.StringReader.
      html/html-resource))

(defn to-iso8601
  "Take a date in the format DD.MM.YYYY and convert it to the ISO-8601
  form YYYY-MM-DD. If components of the date are missing, they will be
  padded with \"01\" as appropriate."
  [date]
  (let [pad (fn [d]
              (if (< (count d) 3)
                (recur (cons "01" d))
                d))]
    (-> date
        (s/split #"[.]")
        pad
        reverse
        (->> (s/join "-")))))

(defn id-from-url
  [url]
  (->> url
       (re-find #"_(\d+)")
       (second)))

(defn tag-from-url
  [url]
  (->> url
       (re-find #"/en/([^/]+)/")
       (second)))

(defn content
  [res]
  (-> res
      :content
      first))
