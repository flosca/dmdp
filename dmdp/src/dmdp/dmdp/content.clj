(ns dmdp.dmdp.content
  (:require [dmdp.layout :as layout]
            [dmdp.db.core :as db]
            [clojure.java.io :as io]
            [ring.util.response :refer [redirect response]]))

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
  ;(if (zero? (mod offset 10))
  (layout/render
   "cat.html" {:publications
               (db/get-publications-from-category
                {:cat_name cat-name
                 :offset offset})

               :offset offset

               :cat_name cat-name

               :next_page (inc page)

               :prev_page (dec page)})))

 ; (layout/render "error.html"))))


(defn authors-page [{:keys [params]}]
  (layout/render
   "content/authors/authors.html" {:authors
                   (db/get-authors
                    {:limit (Integer/parseInt (:limit params "20"))
                     :offset (Integer/parseInt (:offset params "20"))})}))

(defn publications-page [{:keys [params]}]
  (layout/render
   "content/publications/publications.html" {:publications (db/get-publications {:offset (Integer/valueOf (:offset params "20"))
                                                                                 :limit (Integer/valueOf (:limit params "20"))})}))

(defn publication-page [{:keys [params]}]
  (layout/render
   "content/publications/publication.html" {:publication (first (db/get-publication {:id (Integer/valueOf (:id params))}))
                                            :authors (db/get-authors-of-publication {:pub_id (Integer/valueOf (:id params))})}))

(defn author-page [{:keys [params]}]
  (layout/render
   "content/authors/author.html"
     {:author (first (db/get-author {:id (Integer/valueOf (:id params))}))
      :publications (db/get-publications-by-author {:author_id (Integer/valueOf (:id params))})}))
