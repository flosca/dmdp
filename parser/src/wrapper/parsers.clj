(ns wrapper.parsers
  (:require [clojure.xml :as xml]
            [clojure.string :as str]))


(def get-content (mapcat :content))

(defn tagp [pred]
  (comp get-content (filter (comp pred :tag))))

(defn tag= [tag]
  (tagp (partial = tag)))


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


;; Parsers:
(defn get-setSpecs-seq
  [record]
 (->> [record]
      (eduction (comp (tag= :header)
                       get-content))))


(defn get-specifications-list
  [record]
  (map first (map #(eduction (comp get-content) [%])
                  (filter #(= :setSpec (:tag %)) (get-setSpecs-seq record)))))


(defn get-title
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :title)
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
    (zipmap ks fs)))




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
  [record]
  (hash-map :title (get-title record),
            :authors-list (get-authors-list record)
            :category (get-category record)
            :comments (get-comments record)
            :doi (get-doi record)
            :abstract (get-abstract record),
            :specifications-list (get-specifications-list record),
            :journal (get-journal-ref record)
            :date-created (get-date-created record)
            :date-updated (get-date-updated record)))



