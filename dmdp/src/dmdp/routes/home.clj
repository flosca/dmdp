(ns dmdp.routes.home
  (:require [dmdp.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [response redirect]]
            [clojure.java.io :as io]
            [dmdp.db.core :as db]
            [dmdp.dmdp.core :as dmdp]
            [dmdp.dmdp.validators :as validators]))

(defn login-page []
  (layout/render "auth/login.html"))

(defn login! [{:keys [params session]}]
  (if (= (:email params) (:password params))
    (-> (redirect "/")
        (assoc-in [:session :identity] {:name (:username params)}))
    (-> (redirect "/auth/login")
        (assoc-in [:session :identity] nil))))

; Routes

(defroutes private-routes
  (GET "/" req (dmdp/home-page req))
  (GET "/search" req (dmdp/search-page req))
  (GET "/author/:keyname :forenames" req (dmdp/author-page req))
  (GET "/authors" req (dmdp/authors-page req))
  (GET "/p/:id" req (dmdp/publication-page req))
  (GET "/cat/" [] (dmdp/list-of-cats-page))
  (GET "/cat" [] (dmdp/list-of-cats-page))
  (GET "/cat/:cat_name/:page" req (dmdp/cat-page req)))

(defroutes public-routes
  (GET "/auth/login" [] (login-page))
  (POST "/auth/login" req (login! req))
)
