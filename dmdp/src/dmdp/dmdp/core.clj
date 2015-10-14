(ns dmdp.dmdp.core
  (:require [dmdp.layout :as layout]
            [dmdp.db.core :as db]
            [clojure.java.io :as io]
            [ring.util.response :refer [redirect response]]))

(defn home-page [{:keys [session]}]
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)
                 :identity (str (:identity session "None"))}))

(defn search-page [{:keys [params]}]
  (if (empty? params)
    (layout/render
    "search.html")
  (layout/render
    "search.html" {:publications
                   (db/get-publications-by-title
                    {:title (str "%" (:q params) "%")})
                   :query (:q params)})))


(defn authors-page [{:keys [params]}]
  (layout/render
   "authors.html" {:authors
                   (db/get-authors
                    {:limit 20 #_(:limit params)
                     :offset 20 #_(:offset params)})}))

(defn publication-page [{:keys [params]}]
  (layout/render
   "p.html" {:p
             (first
              (db/get-publication {:id (Integer/valueOf (:id params))}))}))

(defn author-page [{:keys [params]}]
  (layout/render
   "author.html" {:author
                   (first (db/get-author-by-name
                          {:keyname (:keyname params)
                           :forenames (:forenames params)}
                   #_{:id (Integer/valueOf (:id params))})),
                  :publications
                  (db/get-publications-by-author-name
                   {:keyname (:keyname params)
                    :forenames (:forenames params)}
                   #_{:author_id (Integer/valueOf (:id params))})}))

#_(defn authors-page-by-letter [{:keys [params]}]
  (layout/render))
