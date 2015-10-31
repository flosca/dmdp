(ns dmdp.routes.home
  (:require [dmdp.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [response redirect]]
            [clojure.java.io :as io]
            [dmdp.db.core :as db]
            [dmdp.dmdp.content :as content]
            [dmdp.dmdp.validators :as validators]
            [dmdp.dmdp.auth :as auth]
            [dmdp.dmdp.profile :as profile]))

; Routes

(defroutes private-routes
  (GET "/" req (content/home-page req))
  (GET "/auth/register" req (auth/register-profile-page req))
  (POST "/auth/register" req (auth/register-profile! req))

  (GET "/profile/edit" req (profile/edit-profile-page req))
  (POST "/profile/edit" req (profile/edit-profile! req))
  (GET "/profile" req (profile/profile-page req))

  (GET "/content/search" req (content/search-page req))
  (GET "/content/authors" req (content/authors-page req))
  (GET "/content/authors/:id" req (content/author-page req))
  (GET "/content/publications" req (content/publications-page req))
  (GET "/content/publications/:id" req (content/publication-page req))
  (GET "/content/categories" req (content/categories-page req))

  #_(GET "/authors/:letter" req (content/authors-page-by-letter req)))

(defroutes public-routes
  (GET "/auth/login" req (auth/login-page req))
  (POST "/auth/login" req (auth/login! req))
  (GET "/auth/logout" req (auth/logout-page req)))
