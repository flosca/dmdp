(ns dmdp.routes.home
  (:require [compojure.core :refer [defroutes GET POST]]
            [dmdp.webapp.content :as content]
            [dmdp.webapp.validators :as validators]
            [dmdp.webapp.auth :as auth]
            [dmdp.webapp.profile :as profile]))

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
  (POST "/content/publications/:id" req (content/delete-publication! req))

  (GET "/content/publications/:id/edit" req (content/edit-publication-page req))
  (POST "/content/publications/:id/edit" req (content/edit-publication! req))

  (GET  "/content/new-publication" req (content/new-publication-page req))
  (POST "/content/new-publication" req (content/add-new-publication! req))
  (GET "/content/categories" req (content/categories-page req)))

(defroutes public-routes
  (GET "/auth/login" req (auth/login-page req))
  (POST "/auth/login" req (auth/login! req))
  (GET "/auth/not-admin" [] (auth/not-admin-page))
  (GET "/auth/logout" req (auth/logout-page req)))
