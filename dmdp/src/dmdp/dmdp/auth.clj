(ns dmdp.dmdp.auth
  (:require [dmdp.layout :as layout]
            [ring.util.response :refer [response redirect]]
            [dmdp.db.core :as db]
            [dmdp.dmdp.validators :as validators]))


(defn login-page []
  (layout/render "auth/login.html"))

(defn login! [{:keys [params session]}]
  (if (validators/validate-login params)
    (-> (redirect "/")
        (assoc-in [:session :identity] {:name (:email params)
                                        :id (:id (first (db/get-user-by-email {:email (:email params)})))}))
    (-> (redirect "/auth/login")
        (assoc-in [:session :identity] nil))))

(defn register-profile-page [{:keys [params]}]
  (layout/render
   "auth/register.html" {}))

(defn register-profile! [{:keys [params]}]
  (if (validators/validate-registration params)
    (do
      (db/create-user! params)
      (redirect "/"))
    (do
      (redirect "/auth/register"))))
