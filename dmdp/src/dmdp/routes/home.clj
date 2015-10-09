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
  (if (= (:username params) (:password params))
    (-> (redirect "/")
        (assoc-in [:session :identity] {:name (:username params)}))
    (-> (redirect "/auth/login")
        (assoc-in [:session :identity] nil))))

; Routes

(defroutes private-routes
  #_(GET "/" req (dmdp/home-page req))
  (GET "/author/:id" req (dmdp/author-page req))
  (GET "/authors" req (dmdp/authors-page req)))

(defroutes public-routes
  (GET "/auth/login" [] (login-page))
  (POST "/auth/login" req (login! req)))
