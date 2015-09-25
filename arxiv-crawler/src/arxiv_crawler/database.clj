(ns arxiv-crawler.database
  (:require [clojure.java.jdbc :as sql])
  (:use     [clojure.string :only (join)]))


(def conn {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost:5432/testdmd3"
           :user "postgres"
           :password "postgres"})

(defn author-query
  [keyname forenames]
  (str "insert into authors (keyname,forenames) "
        "select '" keyname "','" forenames
        "' where not exists "
        "(select id, keyname, forenames "
        "from authors "
        "where keyname = '" keyname
        "' and forenames = '" forenames
        "') returning id"))

(defn authors-list-query
  [authors-list pub-id]
  (loop [lst authors-list
         i 1
         authors-query []
         writes-query []]
    (if (empty? lst) (str "with "
                                   (join ", " authors-query)
                                   " insert into writes values "
                                   (join ", " writes-query) ";")
      (recur (rest lst)
             (inc i)
             (conj authors-query
                 (str "author" i "_id as("
                  (author-query (:keyname (first lst))
                                (:forenames (first lst))) ")"))
             (conj writes-query
                  (str "((select id from author" i "_id), " pub-id ")"))))))





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
      (sql/execute! conn [(authors-list-query (:authors-list parsed-map) pub-id)])))

(defn record-thousand
  [recs]
  (loop [records recs
         i 1]
  (if (empty? records) nil
    (do
      (println i)
      (record-query i (first records))
      (recur (rest records) (inc i))))))

