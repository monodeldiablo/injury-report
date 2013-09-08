(ns injury-report.team-test
  (:require [clojure.test :refer :all]
            [injury-report.team :as t]
            [injury-report.player :as p]))

(deftest scrape-team-profile
  (let [u "/en/fc-arsenal/startseite/verein_11.html"
        arsenal {:id "11"
                 :tag "fc-arsenal"
                 :name "FC Arsenal"}]
    (testing "scraping a team's profile"
      (is (= arsenal (t/profile u))))))

(deftest scrape-squad
  (let [u "/en/fc-arsenal/startseite/verein_11.html"
        u08 "/en/fc-arsenal/historische-kader/verein_11_2008.html"
        arsenal #{["44058" "wojciech-szczesny"]
                  ["24316" "emiliano-viviano"]
                  ["29692" "lukasz-fabianski"]
                  ["6710" "per-mertesacker"]
                  ["15904" "thomas-vermaelen"]
                  ["76277" "laurent-koscielny"]
                  ["43003" "nacho-monreal"]
                  ["44792" "kieran-gibbs"]
                  ["26764" "bacary-sagna"]
                  ["126321" "carl-jenkinson"]
                  ["17396" "mathieu-flamini"]
                  ["93056" "emmanuel-frimpong"]
                  ["7451" "mikel-arteta"]
                  ["74223" "jack-wilshere"]
                  ["50057" "aaron-ramsey"]
                  ["26763" "abou-diaby"]
                  ["132" "tomas-rosicky"]
                  ["35664" "mesut-oezil"]
                  ["15799" "santi-cazorla"]
                  ["15185" "lukas-podolski"]
                  ["129688" "ryo-miyaichi"]
                  ["33713" "theo-walcott"]
                  ["143424" "alex-oxlade-chamberlain"]
                  ["82442" "olivier-giroud"]
                  ["127194" "yaya-sanogo"]
                  ["30982" "chu-young-park"]
                  ["34557" "nicklas-bendtner"]}
        arsenal08 #{["16621" "manuel-almunia"]
                    ["29692" "lukasz-fabianski"]
                    ["33781" "vito-mannone"]
                    ["44058" "wojciech-szczesny"]
                    ["26764" "bacary-sagna"]
                    ["3202" "kolo-toure"]
                    ["3156" "william-gallas"]
                    ["3393" "mikal-silvestre"]
                    ["34561" "johan-djourou"]
                    ["7449" "gal-clichy"]
                    ["44796" "abu-ogogo"]
                    ["13058" "emmanuel-eboue"]
                    ["44792" "kieran-gibbs"]
                    ["44793" "gavin-hoyte"]
                    ["26763" "abou-diaby"]
                    ["8806" "cesc-fbregas"]
                    ["132" "tomas-rosicky"]
                    ["18935" "samir-nasri"]
                    ["42352" "denilson"]
                    ["50057" "aaron-ramsey"]
                    ["27394" "alex-song"]
                    ["74223" "jack-wilshere"]
                    ["32950" "amaury-bischoff"]
                    ["74869" "francis-coquelin"]
                    ["27969" "jay-emmanuel-thomas"]
                    ["44794" "henri-lansbury"]
                    ["42920" "fran-merida"]
                    ["44799" "mark-randall"]
                    ["93056" "emmanuel-frimpong"]
                    ["24633" "eduardo"]
                    ["4380" "robin-van-persie"]
                    ["35773" "carlos-vela"]
                    ["33713" "theo-walcott"]
                    ["15378" "andrey-arshavin"]
                    ["8883" "emmanuel-adebayor"]
                    ["34557" "nicklas-bendtner"]
                    ["9335" "rui-fonte"]
                    ["44801" "jay-simpson"]}]
    (testing "scraping a team's squad"
      (is (= arsenal (apply hash-set (t/squad u))))
      (is (= arsenal08 (apply hash-set (t/squad u08)))))))
