(ns dmdp.dmdp.content
  (:require [dmdp.layout :as layout]
            [dmdp.db.core :as db]
            [clojure.java.io :as io]
            [ring.util.response :refer [redirect response]]
            [dmdp.dmdp.validators :as validators]))

(defn home-page [{:keys [session]}]
  (layout/render
    "home.html" {:identity (str (:identity session "None"))}))

(defn search-page [{:keys [params]}]
  (if (empty? params)
    (layout/render
     "content/search.html")
    (layout/render
     "content/search.html" {:publications (db/get-publications-by-title {:title (str "%" (:q params) "%")})
                           :authors (db/search-author-by-name {:keyname (str "%" (:q params) "%") :forenames (str "%" (:q params) "%")})
                           :query (:q params)})))

(defn list-of-cats-page []
  (layout/render
   "list-of-cats.html" {:categories
               (db/get-categories)}))

(defn- count-publications-in-category [{:keys [params]}]
  (db/count-publications
    {:cat_name (:cat_name params)}))

(defn cat-page [{:keys [params]}]
  (let [cat-name (:cat_name params)
        page (Integer/valueOf (:page params))
        offset (* (dec page) 10)]
  (layout/render
   "cat.html" {:publications
               (db/get-publications-from-category
                {:cat_name cat-name
                 :offset offset})
               :offset offset
               :cat_name cat-name
               :next_page (inc page)
               :prev_page (dec page)})))


(defn authors-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)]
    (if (not= id nil)
  (layout/render
   "content/authors/authors.html" {:user (first (db/get-user {:id (:id (:identity session))}))
                                   :authors
                   (db/get-authors
                    {:limit (Integer/parseInt (:limit params "20"))
                     :offset (Integer/parseInt (:offset params "20"))})})
      (redirect "/auth/login"))))

(defn publications-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)]
    (if (not= id nil)
  (layout/render
   "content/publications/publications.html" {:user (first (db/get-user {:id (:id (:identity session))}))
                                             :publications (db/get-publications {:offset (Integer/valueOf (:offset params "20"))
                                                                                 :limit (Integer/valueOf (:limit params "20"))})})
      (redirect "/auth/login"))))

(defn publication-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)]
    (if (not= id nil)
  (layout/render
   "content/publications/publication.html" {:user (first (db/get-user {:id (:id (:identity session))}))
                                            :publication (first (db/get-publication {:id (Integer/valueOf (:id params))}))
                                            :authors (db/get-authors-of-publication {:pub_id (Integer/valueOf (:id params))})})
      (redirect "/auth/login"))))

(defn new-publication-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)]
    (if (and (not= id nil) (db/check-admin-user {:id id}))
  (layout/render
   "content/publications/new_publication.html" {})
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
                                                       :publication (first (db/get-publication {:id (Integer/valueOf (:id params))}))})
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


(defn author-page [{:keys [params session]}]
(let [id (:id (:identity session nil) nil)]
 (if (not= id nil)
  (layout/render
   "content/authors/author.html"
     {:author (first (db/get-author {:id (Integer/valueOf (:id params))}))
      :publications (db/get-publications-by-author {:author_id (Integer/valueOf (:id params))})})
       (redirect "/auth/login"))))
