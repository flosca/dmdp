(ns arxiv-crawler.database
  (:require [clojure.java.jdbc :as sql])
  (:use     [clojure.string :only (join split)]))


(def conn {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost:5432/testdmd4"
           :user "postgres"
           :password "postgres"})

(defn single-q->double
  [string]
  (if (nil? string) ""
  (str (join "''" (split string #"\'")))))

(defn publication-query
  [pub-id parsed-map]
  (str "insert into publications "
              "values(default,"
               "'" (:uid parsed-map) "', "
               "'" (single-q->double (:title parsed-map)) "', "
               "'" (:date_created parsed-map) "', "
               "'" (:date_updated parsed-map) "', "
               "'" (single-q->double (:journal_ref parsed-map)) "', "
               "'" (single-q->double (:abstract parsed-map)) "', "
               "'" (single-q->double (:doi parsed-map)) "', "
               "'" (:category parsed-map) "', "
               "'" (single-q->double (:comments parsed-map))"');"))

(defn author-query
  [pub-id keyname forenames]
  (str
    "with a1 (keyname, forenames) as ( "
    "values('" (single-q->double keyname) "','"
               (single-q->double forenames) "')), "
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


(defn spec-query
  [pub-id specname]
  (str
    "with s1 (specname) as ( "
    "values('" (single-q->double specname) "')), "
    "try as ( "
    "insert into specifications (specname) "
      "select s1.specname "
      "from s1 "
      "where not exists ( "
        "select * from specifications s "
        "where s.specname = s1.specname) "
            " returning id) "
    "insert into includes values ((select specifications.id "
                           " from specifications "
    "inner join s1 on specifications.specname = s1.specname "
    "union all select try.id from try)," pub-id ");"))


(defn record-query
  [pub-id parsed-map]
  (let [pub (publication-query pub-id parsed-map)]
      (loop [authors-list (:authors-list parsed-map)
             spec-list (:specifications-list parsed-map)
             query ""]
      (if (empty? spec-list)
        (if (empty? authors-list) (str pub query)
      (recur (rest authors-list)
             spec-list
             (str query (author-query pub-id (:keyname (first authors-list))
                                             (:forenames (first authors-list))))))
      (recur authors-list
             (rest spec-list)
             (str query (spec-query pub-id (:specname (first spec-list)))))))))

(defn record-thousand
  [recs pub-id]
  (loop [records recs
         i pub-id
         query "begin; "]
  (if (empty? records)
    (do
    (sql/execute! conn [(str query " commit;")])
    (println i "done!"))
      (recur (rest records)
             (inc i)
             (str query (record-query i (first records)))))))

