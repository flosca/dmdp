(ns web_clj.controllers.index
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [web_clj.templator :as templator]))

(defn index-page [params cookies]
  {:body (templator/render-template "index" {:name (:name params)})})

(defn -routes []
  (GET "/" {params :params cookies :cookies}
         (println (:session params))
         {:body (index-page params cookies)
          :session "Alalal"})
  (GET "/as" {params :params cookies :cookies}
         (println (:session params))
         "qwert"))
