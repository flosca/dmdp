(ns dmdp.dmdp.auth
  (:require [dmdp.layout :as layout]
            [ring.util.response :refer [response redirect]]
            [dmdp.newdb.core :as db]
            [dmdp.dmdp.validators :as validators]
            [buddy.hashers :as hashers]))


(defn login-page [{:keys [session]}]
  (layout/render "auth/login.html" {:identity (:identity session)}))

(defn not-admin-page []
  (layout/render "auth/not-admin.html"))

(defn login! [{:keys [params session]}]
  (if (validators/validate-login params)
    (-> (redirect "/")
        (assoc-in [:session :identity] {:name (:email params)
                                        :id (:id (first (db/get-user-by-email {:email (:email params)})))}))
    (-> (redirect "/auth/login")
        (assoc-in [:session :identity] nil))))

(defn register-profile-page [{:keys [params session]}]
  (layout/render
   "auth/register.html" {:identity (:identity session)}))

(defn register-profile! [{:keys [params]}]
  (if (validators/validate-registration params)
    (do
      (let [salt (str (rand-int 100000))]
      (db/create-user! (assoc params
                         :salt salt
                         :encrypted_pass (hashers/encrypt (:pass params) {:algorithm :pbkdf2+sha256
                                                                          :salt salt}))))
      (redirect "/"))
    (do
      (redirect "/auth/register"))))

(defn logout-page [{:keys [session]}]
  (-> (redirect "/auth/login")
      (assoc-in [:session :identity] nil)))
