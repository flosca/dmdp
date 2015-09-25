(ns arxiv-crawler.api
  (:require [clojure.xml :as xml]
            [arxiv-crawler.time :as t])
  (:use     [clojure.pprint])) ; TODO delete


(def baseurl "http://export.arxiv.org/oai2?verb=ListRecords&from=2007-05-01&until=2010-01-11&metadataPrefix=arXiv")

(def vector-of-dates (t/generate-vector-of-dates))

(defn create-initial-url
  [[start-date end-date]]
  (str "http://export.arxiv.org/"
       "oai2?verb=ListRecords&from="
       start-date
       "&until="
       end-date
       "&metadataPrefix=arXiv"))


(defn generate-monthly-urls
  []
  (loop [dates vector-of-dates
         urls []]
    (if (empty? dates) urls
      (recur (rest dates)
             (conj urls (create-initial-url (first dates)))))))
