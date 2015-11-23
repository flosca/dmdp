(ns dmdp.crawler.database
  (:require [dmdp.dbms.core :refer [insert]]
            [dmdp.dbms.forms :refer [generate-attribute]])
  (:use     [clojure.string :only (join split)]))


(defn publication-query
  [pub-atom parsed-map]
 (insert "data" "dmd.publications"
  [(generate-attribute 0 @pub-atom)
   (generate-attribute 1 (:uid parsed-map))
   (generate-attribute 2 (:title parsed-map))
   (generate-attribute 3 (:date_created parsed-map))
   (generate-attribute 4 (:date_updated parsed-map))
   (generate-attribute 5 (:journal_ref parsed-map))
   (generate-attribute 6 (:abstract parsed-map))
   (generate-attribute 7 (:doi parsed-map))
   (generate-attribute 9 (:comments parsed-map))]))

(defn author-query
  [pub-atom a-atom author-map]

  (insert "data" "dmd.authors"
  [(generate-attribute 0 @a-atom)
   (generate-attribute 1 (:keyname author-map))
   (generate-attribute 2 (:forenames author-map))
   (generate-attribute 3 (:affiliation author-map))])

  (insert "data" "dmd.author_of"
  [(generate-attribute 0 @a-atom)
   (generate-attribute 1 @pub-atom)]))


(defn category-query
  [pub-atom]
  (insert "data" "dmd.category_of"
  [(generate-attribute 0 1)
   (generate-attribute 1 @pub-atom)]))



(defn execute-query
  [pub-atom a-atom parsed-map]
  (loop [authors-list (:authors-list parsed-map)]
    (if (empty? authors-list) (do
                                (swap! pub-atom inc)
                                (publication-query pub-atom parsed-map)
                                (category-query pub-atom)
                                (println @pub-atom "done!"))
    (do
    (swap! a-atom inc)
    (author-query pub-atom a-atom (first authors-list))
    (recur (rest authors-list))))))
