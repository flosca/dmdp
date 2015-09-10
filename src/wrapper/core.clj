(ns wrapper.core
  (require [clojure.xml :as xml]))


(def arxiv (xml/parse "/home/flosca/1000.xml"))

;TODO(def baseurl "http://export.arxiv.org/oai2?verb=ListRecords&from=2015-08-01&until=2015-09-02&metadataPrefix=arXiv")

;TODO(def arxurl (xml/parse (slurp baseurl)))

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

(def records-list (get-records-list arxiv))


;; Parsers:
(defn get-setSpecs-seq
  [record]
 (->> [record]
      (eduction (comp (tag= :header)
                       get-content))))


(defn get-subjects
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

(defn get-journal-ref
  [record]
  (->> [record]
       (eduction (comp (tag= :metadata)
                       (tag= :arXiv)
                       (tag= :journal-ref)
                       get-content))
       first))

(defn parse-record
  [record]
  (hash-map :title (get-title record),
            :authors-list (get-authors-list record),
            :doi (get-doi record)
            :abstract (get-abstract record),
            :setSpecs (get-subjects record),
            :journal (get-journal-ref record)))




(defn -main
  []
  (println "")
  )
