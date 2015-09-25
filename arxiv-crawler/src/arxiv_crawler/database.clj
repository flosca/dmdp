(ns arxiv-crawler.database
  (:require [clojure.java.jdbc :as sql])
  (:use     [clojure.string :only (join split)]))


(def conn {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost:5432/testdmd3"
           :user "postgres"
           :password "postgres"})

(defn single-q->double
  [string]
  (join "''" (split string #"\'")))

(defn author-query
  [pub-id keyname forenames]
  (str
    "with a1 (keyname, forenames) as ( "
    "values('" (single-q->double (str keyname)) "','"
               (single-q->double (str forenames)) "')), "
    "try as ( "
    "insert into authors (keyname,forenames) "
      "select a1.keyname, a1.forenames "
      "from a1 "
      "where not exists ( "
        "select * from authors a "
        "where a.keyname = a1.keyname and "
            " a.forenames = a1.forenames)"
            " returning id) "
    "insert into writes values ((select authors.id "
                           " from authors "
    "inner join a1 on authors.keyname = a1.keyname "
             "and authors.forenames = a1.forenames "
    "union all select try.id from try)," pub-id ");"))


(defn record-query
  [pub-id parsed-map]
    (do
      (sql/insert! conn :publications (select-keys parsed-map
                                                   [:title
                                                    :uid
                                                    :date_created
                                                    :date_updated
                                                    :journal_ref
                                                    :abstract
                                                    :doi
                                                    :category
                                                    :comments]))
      (loop [authors-list (:authors-list parsed-map)]
        (if (empty? authors-list) nil
      (do
      (sql/execute! conn [(author-query pub-id (:keyname (first authors-list))
                                               (:forenames (first authors-list)))])
      (recur (rest authors-list)))))))

(defn record-thousand
  [recs]
  (loop [records recs
         i 1]
  (if (empty? records) nil
    (do
      (println i)
      (record-query i (first records))
      (recur (rest records) (inc i))))))

