(ns dmdp.dbms.queries
  (:use
    [dmdp.dbms.core]
    [dmdp.dbms.forms]
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
  (select "data" "dmd.users" [[0 #(= (Integer/valueOf (:id params)) %)]]))


(defn get-user-by-email
  [params]
  (println params)
  (select "data" "dmd.users" [[3 #(= (:email params) %)]]))


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
  [params]
  (select "data" "dmd.authors" [[1 #(like (:keyname params) %)]
                                [2 #(like (:forenames params) %)]] "or"))

(defn get-category-name-by-id
  [])

(defn get-categories
  []
  (project "data" "dmd.categories"))

(defn get-authors [params]
  (take (:limit params) (drop (:offset params)
  (sort-by :keyname (project "data" "dmd.authors")))))

(defn get-publications-by-author [params]
  (mapcat (fn [e] (select "data" "dmd.publications" [[0 #(= e %)]]))
  (map :publication_id (select "data" "dmd.author_of" [[0 #(= (Integer/valueOf (:author_id params)) %)]]))))


(defn get-publications
  [params]
  (take (:limit params) (drop (:offset params)
  (sort-by :title (project "data" "dmd.publications")))))

(defn get-publication [id]
  (select "data" "dmd.publications" [[0 #(= (Integer/valueOf id) %)]]))

(defn get-authors-of-publication [params]
  (mapcat (fn [e] (select "data" "dmd.authors" [[0 #(= e %)]]))
  (map :author_id (select "data" "dmd.author_of" [[1 #(= (Integer/valueOf (:pub_id params)) %)]]))))

(defn get-publication-categories [params])

(defn check-admin-user [id]
  (:admin (first (select "data" "dmd.users" [[0 #(= (Integer/valueOf id) %)]]))))


(defn create-publication [params]
  (let [id (rand-int 1000)]
  (insert "data" "dmd.publications"
     [(generate-field 0 id)
      (generate-field 1 (:uid params))
      (generate-field 2 (:title params))
      (generate-field 3 (:date_created params))
      (generate-field 4 (:date_updated params))
      (generate-field 5 (:journal_ref params))
      (generate-field 6 (:abstract params))
      (generate-field 7 (:doi params))
      (generate-field 8 (:isbn params))
      (generate-field 9 (:comments params))])
    id))


(defn create-author [params]
  (let [id (rand-int 1000)]
  (insert "data" "dmd.authors"
     [(generate-field 0 id)
      (generate-field 1 (:keyname params))
      (generate-field 2 (:forenames params))
      (generate-field 3 (:affiliation params))])
      id))

(defn bind-publication-to-author! [params]
  (insert "data" "dmd.author_of"
     [(generate-field 0 (:author_id params))
      (generate-field 1 (:publication_id params))]))

(defn update-publication! [params])

(defn get-publications-by-title
[params]
 (take (:limit params) (drop (:offset params) (sort-by :title
   (select "data" "dmd.publications" [[2 #(like (:title params) %)]])))))

(defn get-author
  [params]
  (select "data" "dmd.authors" [[0 #(= (Integer/valueOf (:id params)) %)]]))


;; Delete Queries

(defn delete-publication
  [params]
  (let [record
    (reduce (fn [c v]
      (filter #(and (= 0 (:attribute-id %))
                    (= (:id params) (:value %))) v) c)
       (projection "data" "dmd.publications"))]
  (remove-record "data" "dmd.publications" record)))
