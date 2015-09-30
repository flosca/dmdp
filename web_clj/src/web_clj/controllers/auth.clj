(ns web_clj.controllers.auth
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [web_clj.templator :as templator]))

(defn register [] "Registration")

(defn submit-register [] "Submit registration")

(defn login []
  (templator/render-template "auth/login" []))

(defn submit-login [params]
  (if (= (:login params) (:password params))
    (def session {:auth true})
    (def session {:auth false}))
  (println (str "session" (:session params)))
  {:body (templator/render-template "auth/login" params)
   :session session})

(defn forgot-password [] "Forgot password")

(defn submit-forgot-password [] "Submit forgot password")

(defn -routes [params]
  (GET "/login" {params :params cookies :cookies}
       (println "GET_LOGIN")
       (login []))
  (POST "/login" {params :params cookies :cookies}
        (submit-login [params])))
