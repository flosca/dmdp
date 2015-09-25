(ns arxiv-crawler.parsers
  (:require [clojure.xml :as xml]
            [clojure.string :as str]
            [arxiv-crawler.api :as api]
            [arxiv-crawler.database :as db])
  (:use     [clojure.pprint])) ; TODO delete

(def arxiv (xml/parse "/home/flosca/1000.xml"))


; Preparations for parsing clojure collections generated from xml.

(def get-content (mapcat :content))

(defn tag= [tag]
  (comp get-content
        (filter (comp (partial = tag) :tag))))

(defn get-records-list
  [xmldoc]
    (eduction (comp (tag= :ListRecords)
                    (tag= :record))
                [xmldoc]))

(defn get-token
  [xmldoc]
  (->> [xmldoc]
       (eduction (comp (tag= :ListRecords)
                       (tag= :resumptionToken)
                        get-content))
        first))


(defn get-seq-from-header
  [record]
 (->> [record]
      (eduction (comp (tag= :header)
                       get-content))))


(defn get-specifications-list
  [record]
  (map #(hash-map :specname %)
  (map first (map #(eduction (comp get-content) [%])
                  (filter #(= :setSpec (:tag %)) (get-seq-from-header record))))))


(defn get-title
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :title)
                        get-content))
       first))

(defn get-uid
  "Universal id for each article (to download pdf/postscripts)"
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :id)
                        get-content))
       first))

(defn get-authors-seq
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :authors)
                       get-content))))


(defn get-keynames-list
  [sq]
  (eduction (comp  (tag= :keyname)
                    get-content) [sq]))

(defn get-forenames-list
  [sq]
  (eduction (comp  (tag= :forenames)
                    get-content) [sq]))

(defn get-authors-list
   [record]
(let [sq (get-authors-seq record)
      ks (map first (map get-keynames-list sq))
      fs (map first (map get-forenames-list sq))]
    (map #(hash-map :keyname (first %)
                     :forenames (second %))
           (zipmap ks fs))))




(defn get-abstract
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :abstract)
                       get-content))
       first))


(defn get-doi
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :doi)
                       get-content))
       first))

(defn get-date-created
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :created)
                       get-content))
       first))

(defn get-date-updated
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :updated)
                       get-content))
       first))

(defn get-category
  [record]
    (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :categories)
                       get-content))
       first))

(defn get-journal-ref
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :journal-ref)
                       get-content))
       first))

(defn get-comments
  [record]
    (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :comments)
                       get-content))
       first))


(defn parse-record
  "Takes a clojure collection and returns a hsh-map with keys as attributes for tables."
  [record]
  (hash-map :uid (get-uid record)
            :title (get-title record)
            :authors-list (get-authors-list record)
            :category (get-category record)
            :comments (get-comments record)
            :doi (get-doi record)
            :abstract (get-abstract record),
            :specifications-list (get-specifications-list record),
            :journal_ref (get-journal-ref record)
            :date_created (get-date-created record)
            :date_updated (get-date-updated record)))




(def rec (->> arxiv get-records-list
                   (map parse-record)))




(defn -main
  []
 (pprint
  #_(:authors-list (first rec))
  #_(db/record-thousand (drop 10 rec))
  (nth (map parse-record (get-records-list arxiv)) 15)))
