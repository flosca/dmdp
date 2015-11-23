(ns dmdp.crawler.parsers
  (:require [clojure.xml :as xml]
            [clojure.data.xml :as data-xml]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [dmdp.crawler.time :as t]
            [dmdp.crawler.database :as db]))

(defn xml->coll
  [xml]
  (data-xml/parse (io/reader xml)))

(def get-content (mapcat :content))

(defn tag= [tag]
  (comp get-content
        (filter (comp (partial = tag) :tag))))

(defn get-records-list
  [xml-coll]
    (eduction (comp (tag= :dblp)
                    (tag= :article))
                [xml-coll]))

(defn get-records-list-arxiv
  [xml-coll]
    (eduction (comp (tag= :ListRecords)
                    (tag= :record))
                [xml-coll]))

(defn article?
  [record]
  (= :article (:tag record)))

(defn get-authors-list
  [record]
 (let [lst (eduction (comp (tag= :author)
                   get-content)
            [record])]
  (map #(hash-map :keyname %
                  :forenames ""
                  :affiliation "") lst)))


(defn fix-title-tags
  [title]
  (if (string? title) title
    (-> title
        :content
        first)))

(defn get-title
  [record]
  (let [title (->> [record]
       (eduction (comp (tag= :title)
                        get-content)))]
    (if (string? title) title
      (->> title
          (map fix-title-tags)
           (reduce str)))))


(defn get-uid
  [record]
   (->> [record]
       (eduction (comp (tag= :url)
                        get-content))
       first))

(defn get-doi
  [record]
   (->> [record]
       (eduction (comp (tag= :ee)
                        get-content))
       first))


(defn get-abstract
  [record]
  "")

(defn get-journal-ref
  [record]
    (->> [record]
       (eduction (comp (tag= :journal)
                        get-content))
       first))

(defn get-year
  [record]
    (->> [record]
       (eduction (comp (tag= :year)
                        get-content))
       first))

(defn get-pages
  [record]
    (->> [record]
       (eduction (comp (tag= :pages)
                        get-content))
       first))

(defn get-volume
  [record]
    (->> [record]
       (eduction (comp (tag= :volume)
                        get-content))
       first))

(defn get-comments
  [record]
  (let [year (get-year record)
        pages (get-pages record)
        volume (get-volume record)
        delimiter (if (nil? (and pages volume)) "" ", ")]
    (str (if (nil? volume) " " (str "Volume: " volume delimiter))
         (if (nil? pages)  " " (str "Pages: " pages delimiter))
         (if (nil? year)   " " (str "Year: " year ".")))))


(defn get-date-created
  [record]
    (let [date (-> record
                    :attrs
                    :mdate)]

    (if (nil? date) (t/current-time) date)))

(defn get-date-updated
  [record]
     (t/current-time))


(defn parse-record
  "Takes a clojure collection and returns a hash-map with keys as attributes for tables."
  [record]
  (hash-map :uid (get-uid record)
            :title (get-title record)
            :authors-list (get-authors-list record)
            :comments (get-comments record)
            :doi (get-doi record)
            :abstract (get-abstract record),
            :journal_ref (get-journal-ref record)
            :date_created (get-date-created record)
            :date_updated (get-date-updated record)))

(def p-atom (atom 1))
(def a-atom (atom 0))

(defn parse-dblp
[]
(doseq [seq (take 1000 (:content (xml->coll "tables/dblp.xml")))
         :when (article? seq)]
 (db/execute-query p-atom
                   a-atom
                   (parse-record seq))))
