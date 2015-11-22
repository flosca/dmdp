(ns dmdp.newdb.core
  (:use
    [dmdp.dbms.db]
    [dmdp.dbms.utils]))

(defn prepare-database
  []
(let [db-title "data"]
  (delete-database db-title)
  (initialize-database db-title)

 ; Publication tables

  (add-table db-title "dmd.authors"
   [(generate-attribute 0 "id" 1 0)
    (generate-attribute 1 "keyname" 0 0)
    (generate-attribute 2 "forenames" 0 0)
    (generate-attribute 3 "affiliation" 0 0)])

  (add-table db-title "dmd.publications"
   [(generate-attribute 0 "id" 1 0)
    (generate-attribute 1 "uid" 0 0)
    (generate-attribute 2 "title" 0 0)
    (generate-attribute 3 "date_created" 0 0)
    (generate-attribute 4 "date_updated" 0 0)
    (generate-attribute 5 "journal_ref" 0 0)
    (generate-attribute 6 "abstract" 0 0)
    (generate-attribute 7 "doi" 0 0)
    (generate-attribute 8 "isbn" 0 0)
    (generate-attribute 9 "comments" 0 0)])

  (add-table db-title "dmd.categories"
   [(generate-attribute 0 "id" 1 0)
    (generate-attribute 1 "category_name" 0 0)])

  (add-table db-title "dmd.author_of"
   [(generate-attribute 0 "author_id" 1 0)
    (generate-attribute 1 "publication_id" 1 0)])


  (add-table db-title "dmd.category_of"
   [(generate-attribute 0 "category_id" 1 0)
    (generate-attribute 1 "publication_id" 1 0)])

  ; Web app table

  (add-table db-title "dmd.users"
   [(generate-attribute 0 "id" 1 0)
    (generate-attribute 1 "first_name" 0 0)
    (generate-attribute 2 "last_name" 0 0)
    (generate-attribute 3 "email" 0 0)
    (generate-attribute 4 "admin" 0 0)
    (generate-attribute 5 "pass" 0 0)
    (generate-attribute 6 "salt" 0 0)])))


;; Queries

(defn get-user
  [params]
  (select "data" "dmd.users" [[0 (fn [v] (= v (:id params)))]]))


(defn get-user-by-email
  [params]
  (println params)
  (select "data" "dmd.users" [[3 (fn [v] (= v (:email params)))]]))


(defn update-user!
  []
  )

(defn create-user!
  [params]
  (insert "data" "dmd.users" [(generate-field 0 0)
                              (generate-field 1 (:first_name params))
                              (generate-field 2 (:last_name params))
                              (generate-field 3 (:email params))
                              (generate-field 4 true)
                              (generate-field 5 (:encrypted_pass params))
                              (generate-field 6 (:salt params))]))


(defn get-publications-by-title-from-category
  [])


(defn get-publications-from-category-by-category-id
  [])


(defn search-author-by-name
  [])

(defn get-category-name-by-id
  [])

(defn get-categories
  [])

(defn get-authors [])

(defn get-publications-by-author [])


(defn get-publications [])

(defn get-publication [params]
  (select "data" "dmd.publications" [[0 (fn [v] (= v (:id params)))]]))

(defn get-authors-of-publication [params])

(defn get-publication-categories [params])

(defn check-admin-user [])

(defn create-publication [])


(defn create-author [])

(defn bind-publication-to-author! [])

(defn update-publication! [])

(defn get-publications-by-title
[])

(defn get-author [])
