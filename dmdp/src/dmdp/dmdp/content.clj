(ns dmdp.dmdp.content
  (:require [dmdp.layout :as layout]
            [dmdp.db.core :as db]
            [clojure.java.io :as io]
            [ring.util.response :refer [redirect response]]
            [dmdp.dmdp.validators :as validators]
            [clojure.string :refer [split]]))

(defn home-page [{:keys [session]}]
  (layout/render
    "home.html" {:identity (:identity session)}))

(defn search-page [{:keys [params session]}]
  (if (empty? params)
    (layout/render
     "content/search.html" {:identity (:identity session)})
      (let [limit (Integer/parseInt(:limit params "20"))
            offset (Integer/parseInt(:offset params "0"))
            title (let [query (:q params)] (if (= query nil) nil (str "%" query "%")))
            category_id (let [category (:category params)] (if (= category nil) nil (Integer/parseInt category)))]
      (layout/render
       "content/search.html" {:publications
                              (cond
                               (and (not= category_id nil) (not= title nil)) (db/get-publications-by-title-from-category
                                                                                                  {:category_id category_id
                                                                                                   :title title
                                                                                                   :limit limit
                                                                                                   :offset offset}) ; by title and category
                               (not= category_id nil) (db/get-publications-from-category-by-category-id
                                                       {:category_id category_id
                                                        :limit limit
                                                        :offset offset})
                               (not= title nil) (db/get-publications-by-title {:title title
                                                                               :limit limit
                                                                               :offset offset})
                               :else (do (println "No filter!\n") []))
                             :authors (db/search-author-by-name {:keyname (str "%" (:q params) "%") :forenames (str "%" (:q params) "%")})
                             :query (:q params)
                             :category_name (if (= category_id nil) nil (:category_name (first (db/get-category-name-by-id {:id category_id}))))
                             :category_id category_id
                             :identity (:identity session)
                             :prev_page_offset (if (< (- offset limit) 0) 0 (- offset limit))
                             :next_page_offset (+ offset limit)}))))


(defn categories-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)]
    (if (not= id nil)
  (layout/render
   "content/categories/categories.html" {:categories (db/get-categories)
                                         :identity (:identity session)})
  (redirect "/auth/login"))))

(defn authors-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)
        limit (Integer/parseInt (:limit params "10"))
        offset (Integer/parseInt (:offset params "0"))]
    (if (not= id nil)
    (layout/render
     "content/authors/authors.html" {:authors
                     (db/get-authors
                      {:limit limit
                       :offset offset
                       :identity (:identity session)})
                                     :prev_page_offset (if (< (- offset limit) 0) 0 (- offset limit))
                                     :next_page_offset (+ offset limit)})
       (redirect "/auth/login"))))


(defn author-page [{:keys [params session]}]
    (let [user-id (:id (:identity session nil) nil)]
  (if (not= user-id nil)
  (layout/render
   "content/authors/author.html"
     {:author (first (db/get-author {:id (Integer/valueOf (:id params))}))
      :publications (db/get-publications-by-author {:author_id (Integer/valueOf (:id params))})
      :identity (:identity session)})
          (redirect "/auth/login"))))


(defn publications-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)
        limit (Integer/valueOf (:limit params "10"))
        offset (Integer/valueOf (:offset params "0"))
        order_by (str (nth (split (:sort_by params "date_updated-asc") #"-") 0) " " (nth (split (:sort_by params "date_updated-asc") #"-") 1))]
    ;(println order_by)
   (if (not= id nil)
  (layout/render
   "content/publications/publications.html" {:identity (:identity session)
                                             :publications (db/get-publications {:offset offset
                                                                                 :limit limit
                                                                                 :order_by order_by})
                                             :prev_page_offset (if (< (- offset limit) 0) 0 (- offset limit))
                                             :next_page_offset (+ offset limit)
                                             :sort_by (:sort_by params "title-asc")})
  (redirect "/auth/login"))))


(defn publication-page [{:keys [params session]}]
  (let [pub-id (Integer/parseInt (:id params))
        user-id (:id (:identity session nil) nil)]
  (if (not= user-id nil)
    (layout/render
     "content/publications/publication.html" {:publication (first (db/get-publication {:id pub-id}))
                                              :authors (db/get-authors-of-publication {:pub_id pub-id})
                                              :categories (db/get-publication-categories {:publication_id pub-id})
                                              :identity (:identity session)})
      (redirect "/auth/login"))))


(defn new-publication-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)]
    (if (and (not= id nil) (db/check-admin-user {:id id}))
  (layout/render
   "content/publications/new_publication.html" {:identity (:identity session)})
      (redirect "/auth/not-admin"))))

(defn add-new-publication! [{:keys [params]}]
  (do
    (let [pub-id (:id (first (db/create-publication params)))
          a-id   (:id (first (db/create-author params)))]

    (db/bind-publication-to-author! {:author_id a-id
                                     :publication_id pub-id})

    (redirect (str "/content/publications/" pub-id)))))

(defn edit-publication-page [{:keys [params session]}]
(let [id (:id (:identity session nil) nil)]
    (if (and (not= id nil) (db/check-admin-user {:id id}))
      (layout/render
         "content/publications/edit_publication.html" {:user (first (db/get-user {:id (:id (:identity session))}))
                                                       :publication (first (db/get-publication {:id (Integer/valueOf (:id params))}))
                                                       :identity (:identity session)})
      (redirect (str "/content/publications/" (:id params))))))

(defn edit-publication! [{:keys [params]}]
  (if (validators/validate-publication-edition params)
    (do
      (db/update-publication!
                       {:id (Integer/valueOf (:id params))
                        :title (:title params)
                        :uid (:uid params)
                        :abstract (:abstract params)
                        :doi (:doi params)
                        :journal_ref (:journal_ref params)
                        :comments (:comments params)
                        :date_created (:date_created params)
                        :date_updated (:date_updated params)})
      (redirect (str "/content/publications/" (:id params))))
    (redirect (str "/content/publications/" (:id params) "/edit"))))


