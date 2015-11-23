(ns dmdp.webapp.validators
  (:require [bouncer.core :as bouncer]
            [bouncer.validators :as validators]
            [dmdp.dbms.queries :as db]
            [buddy.hashers :as hashers]))

(defn is-user-unique? [email]
  (let [user-unique (first (db/get-user-by-email email))]
    (nil? user-unique)))

(defn user-can-login? [email password]
  (let [user (first (db/get-user-by-email {:email email}))]
    (and (not= user nil)
         (hashers/check password (:pass user)))))

; -- validators

(defn validate-login [params]
  (and (bouncer/valid?
         params
         :email validators/required
         :pass validators/required)
       (user-can-login? (:email params) (:pass params))))

(defn validate-registration [params]
  (and (bouncer/valid?
         params
         :first_name validators/required
         :last_name validators/required
         :email validators/required
         :pass validators/required)
       (is-user-unique? params)))

(defn validate-user-edition [params]
  (bouncer/valid?
   params
   :first_name validators/required
   :last_name validators/required
   :email validators/required))

(defn validate-publication-edition [params]
  (bouncer/valid?
   params
   :title validators/required))
