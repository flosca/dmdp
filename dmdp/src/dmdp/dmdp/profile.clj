(ns dmdp.dmdp.profile
  (:require [dmdp.layout :as layout]
            [ring.util.response :refer [response redirect]]
            [dmdp.newdb.core :as db]
            [dmdp.dmdp.validators :as validators]))

(defn profile-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)]
    (if (not= id nil)
      (layout/render
         "profile/profile.html" {:user (first (db/get-user {:id id}))
                                 :identity (:identity session)})
      (redirect "/auth/login"))))

(defn edit-profile-page [{:keys [params session]}]
  (let [id (:id (:identity session nil) nil)]
    (if (not= id nil)
      (layout/render
         "profile/edit_profile.html" {:user (first (db/get-user {:id (:id (:identity session))}))
                                      :identity (:identity session)})
      (redirect "/auth/login"))))

(defn edit-profile! [{:keys [params session]}]
  (if (validators/validate-user-edition params)
    (do
      (db/update-user! {:id (:id (:identity session))
                        :first_name (:first_name params)
                        :last_name (:last_name params)
                        :email (:email params)})
      (redirect "/profile"))
    (redirect "/profile/edit")))
