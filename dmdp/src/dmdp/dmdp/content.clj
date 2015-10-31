(ns dmdp.dmdp.content
  (:require [dmdp.layout :as layout]
            [dmdp.db.core :as db]
            [clojure.java.io :as io]
            [ring.util.response :refer [redirect response]]))

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
                               (and (not= category_id nil) (not= title nil)) (do (println "1\n") (db/get-publications-by-title-from-category
                                                                                                  {:category_id category_id
                                                                                                   :title title
                                                                                                   :limit limit
                                                                                                   :offset offset})) ; by title and category
                               (not= category_id nil) (db/get-publications-from-category-by-category-id
                                                       {:category_id category_id
                                                        :limit limit
                                                        :offset offset})
                               (not= title nil) (db/get-publications-by-title {:title title})
                               :else (do (println "No filter!\n") []))
                             :authors (db/search-author-by-name {:keyname (str "%" (:q params) "%") :forenames (str "%" (:q params) "%")})
                             :query (:q params)
                             :category_name (if (= category_id nil) nil (:category_name (first (db/get-category-name-by-id {:id category_id}))))
                             :identity (:identity session)}))))

(defn categories-page [{:keys [params session]}]
  (layout/render
   "content/categories/categories.html" {:categories (db/get-categories)
                                         :identity (:identity session)}))

(defn authors-page [{:keys [params session]}]
  (layout/render
   "content/authors/authors.html" {:authors
                   (db/get-authors
                    {:limit (Integer/parseInt (:limit params "20"))
                     :offset (Integer/parseInt (:offset params "20"))
                     :identity (:identity session)})}))

(defn publications-page [{:keys [params session]}]
  (layout/render
   "content/publications/publications.html" {:publications (db/get-publications {:offset (Integer/valueOf (:offset params "20"))
                                                                                 :limit (Integer/valueOf (:limit params "20"))
                                                                                 :identity (:identity session)})}))

(defn publication-page [{:keys [params session]}]
  (let [id (Integer/parseInt (:id params))]
    (layout/render
     "content/publications/publication.html" {:publication (first (db/get-publication {:id id}))
                                              :authors (db/get-authors-of-publication {:pub_id id})
                                              :categories (db/get-publication-categories {:publication_id id})
                                              :identity (:identity session)})))

(defn author-page [{:keys [params session]}]
  (layout/render
   "content/authors/author.html"
     {:author (first (db/get-author {:id (Integer/valueOf (:id params))}))
      :publications (db/get-publications-by-author {:author_id (Integer/valueOf (:id params))})
      :identity (:identity session)}))
