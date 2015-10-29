(ns dmdp.routes.home
  (:require [dmdp.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [response redirect]]
            [clojure.java.io :as io]
            [dmdp.db.core :as db]
            [dmdp.dmdp.core :as dmdp]
            [dmdp.dmdp.auth :as auth]))

; Routes

(defroutes private-routes
  (GET "/" req (dmdp/home-page req))
  (GET "/auth/profile/:id" req (auth/profile-page req))
  (GET "/auth/register" req (auth/register-profile-page req))
  (POST "/auth/register" req (auth/register-profile! req))

  (GET "/profile/edit/:id" req (auth/edit-profile-page req))
  (POST "/profile/edit/:id" req (auth/edit-profile! req))

  (GET "/content/search" req (dmdp/search-page req))
  (GET "/content/authors/:id" req (dmdp/author-page req))
  (GET "/content/authors" req (dmdp/authors-page req))
  (GET "/content/:id" req (dmdp/publication-page req))
  #_(GET "/authors/:letter" req (dmdp/authors-page-by-letter req)))

(defroutes public-routes
  (GET "/auth/login" [] (auth/login-page))
  (POST "/auth/login" req (auth/login! req)))
