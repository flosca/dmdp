(ns dmdp.crawler.database
  (:require [dmdp.dbms.core :refer [insert]]
            [dmdp.dbms.forms :refer [generate-field]])
  (:use     [clojure.string :only (join split)]))


(defn publication-query
  [pub-atom parsed-map]
 (insert "data" "dmd.publications"
  [(generate-field 0 @pub-atom)
   (generate-field 1 (:uid parsed-map))
   (generate-field 2 (:title parsed-map))
   (generate-field 3 (:date_created parsed-map))
   (generate-field 4 (:date_updated parsed-map))
   (generate-field 5 (:journal_ref parsed-map))
   (generate-field 6 (:abstract parsed-map))
   (generate-field 7 (:doi parsed-map))
   (generate-field 9 (:comments parsed-map))]))

(defn author-query
  [pub-atom a-atom author-map]

  (insert "data" "dmd.authors"
  [(generate-field 0 @a-atom)
   (generate-field 1 (:keyname author-map))
   (generate-field 2 (:forenames author-map))
   (generate-field 3 (:affiliation author-map))])

  (insert "data" "dmd.author_of"
  [(generate-field 0 @a-atom)
   (generate-field 1 @pub-atom)]))


(defn category-query
  [pub-atom]
  (insert "data" "dmd.category_of"
  [(generate-field 0 1)
   (generate-field 1 @pub-atom)]))



(defn execute-query
  [pub-atom a-atom parsed-map]
  (loop [authors-list (:authors-list parsed-map)]
    (if (empty? authors-list) (do
                                (publication-query pub-atom parsed-map)
                                (category-query pub-atom)
                                (swap! pub-atom inc))
    (do
    (author-query pub-atom a-atom (first authors-list))
    (swap! a-atom inc)
    (recur (rest authors-list))))))
