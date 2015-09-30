(ns web_clj.core
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.response :as response]
            [ring.middleware.params :as params]
            [ring.middleware.keyword-params :as keyword-params]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.session :as session]
            [web_clj.controllers.auth :as auth-controller]
            [web_clj.controllers.index :as index-controller]
            [web_clj.wrappers.auth :as auth-wrapper]
            [web_clj.templator :as templator]
            #_[org.clojure/clojure "1.6.0"]
            #_[incanter "1.5.6"]))

(defroutes app-routes
  (context "/" []
           (GET "/" {params :params cookies :cookies}
                (println (:session params))
                 (merge {:session "Alalal"}
                        (index-controller/index-page params cookies)))
           (GET "/as" {params :params cookies :cookies}
                (println (:session params))
                "qwert")
           #_(index-controller/-routes))

  (context "/auth" []
           (GET "/login" {params :params cookies :cookies}
                (auth-controller/login))
           (POST "/login" {params :params cookies :cookies}
                (auth-controller/submit-login params))
           #_(auth-controller/-routes params))
  (route/not-found (templator/render-template "errors/404" [])))

(def app
  (-> app-routes
      cookies/wrap-cookies
      keyword-params/wrap-keyword-params
      params/wrap-params
      auth-wrapper/wrap-auth
      session/wrap-session))
