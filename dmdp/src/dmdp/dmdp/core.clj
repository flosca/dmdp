(ns dmdp.dmdp.core
  (:require [dmdp.layout :as layout]
            [dmdp.db.core :as db]
            [clojure.java.io :as io]
            [ring.util.response :refer [redirect response]]))

(defn home-page [{:keys [session]}]
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)
                 :identity (str (:identity session "None"))}))

(defn authors-page [{:keys [params]}]
  (layout/render
   "authors.html" {:authors
                   (db/get-authors
                    {:limit (:limit params)
                     :offset (:offset params)})}))

(defn author-page [{:keys [params]}]
  (layout/render
   "author.html" {:author
                  (db/get-author
                   {:id (:id params)})}))
