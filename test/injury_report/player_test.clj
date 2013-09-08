(ns injury-report.player-test
  (:require [clojure.test :refer :all]
            [injury-report.player :as p]
            [injury-report.team :as t]))

(deftest scrape-player-profile
  (let [ou "/en/mesut-oezil/profil/spieler_35664.html"
        ozil {:id "35664"
              :tag "mesut-oezil"
              :name "Mesut Özil"
              :given-name "Mesut Özil"
              :birthdate "1988-10-15"
              :birthplace "Gelsenkirchen"
              :height "1,83"
              :foot "left"}
        su "/en/wojciech-szczesny/profil/spieler_44058.html"
        szczesny {:id "44058"
                  :tag "wojciech-szczesny"
                  :name "Wojciech Szczesny"
                  :given-name "Wojciech Tomasz Szczęsny"
                  :birthdate "1990-04-18"
                  :birthplace "Warschau"
                  :height "1,96"
                  :foot "right"}]
    (testing "scraping a player's vitals"
      (is (= ozil (p/profile ou)))
      (is (= szczesny (p/profile su))))))

(deftest scrape-injury-records
  (let [u "/en/mesut-oezil/profil/spieler_35664.html"
        injuries #{{:pid "35664" :s "2010-03-12" :e "2010-03-18" :c "back trouble"}
                   {:pid "35664" :s "2009-12-04" :e "2009-12-09" :c "Flu"}
                   {:pid "35664" :s "2009-09-15" :e "2009-09-25" :c "Knee Problems"}
                   {:pid "35664" :s "2009-05-01" :e "2009-05-04" :c "Knee Problems"}
                   {:pid "35664" :s "2009-04-17" :e "2009-04-20" :c "Knee Problems"}
                   {:pid "35664" :s "2009-03-06" :e "2009-03-13" :c "tear in a joint capsule"}
                   {:pid "35664" :s "2007-11-06" :e "2007-11-12" :c "Stretched Ligament"}
                   {:pid "35664" :s "2007-10-17" :e "2007-11-02" :c "Torn Ankle Joint Ligament"}}]
    (testing "scraping a player's injury records"
      (is (= injuries (apply hash-set (p/injuries u)))))))

(deftest scrape-transfer-history
  (let [u "/en/mesut-oezil/transfers/spieler_35664.html"
        transfers #{{:pid "35664" :cid "11" :d "2013-09-02" :f 50000000}
                    {:pid "35664" :cid "418" :d "2010-08-17" :f 18000000}
                    {:pid "35664" :cid "86" :d "2008-01-31" :f 5000000}
                    {:pid "35664" :cid "33" :d "2006-07-01" :f 0}
                    {:pid "35664" :cid "1463" :d "2005-07-01" :f 0}
                    {:pid "35664" :cid "21073" :d "2003-07-01" :f 0}
                    {:pid "35664" :cid "11877" :d "2000-07-01" :f 0}
                    {:pid "35664" :cid "39720" :d "1999-07-01" :f 0}
                    {:pid "35664" :cid "39721" :d "1998-07-01" :f 0}}]
    (testing "scraping a player's transfer history"
      (is (= transfers (apply hash-set (p/transfers u)))))))
