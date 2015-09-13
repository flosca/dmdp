(ns wrapper.core
  (:require
   [wrapper.parsers :as p]
   [clojure.xml :as xml]
   [clj-http.client :as http]))



(def arxiv (xml/parse "/home/flosca/1000.xml"))
;(def records-list (p/get-records-list arxiv))

(def baseurl "http://export.arxiv.org/oai2?verb=ListRecords&from=2010-01-01&until=2010-01-11&metadataPrefix=arXiv")

;(def parsed-recs (map p/parse-record records-list))


#_(defn checked-status
  [url]
  (let [status (-> url
                   (http/get {:throw-exceptions false})
                   :status)]
   (if (= status 200)
     url
      (do
        (println "Server error, wait for 20 sec")
        (Thread/sleep 21000)
        url))))

(defn crawl-xml
  [url]
  (loop [coll []
         l-url url
         i 1]
  (let [parsed-xml (xml/parse l-url)
        records-list (->>  parsed-xml
                           p/get-records-list
                           (map p/parse-record))
        token (p/get-token parsed-xml)
        tokenized-url (str "http://export.arxiv.org/oai2?verb=ListRecords&resumptionToken=" token)]
    (if (nil? token)
      (do
      (Thread/sleep 21000)
      (println "last step complete")
      (into coll records-list))
      (do
      (Thread/sleep 21000)
      (println (str i "step complete"))
      (recur (into coll records-list) tokenized-url (inc i)))))))


(defn -main
  []
  (println
  (count (crawl-xml baseurl))))


