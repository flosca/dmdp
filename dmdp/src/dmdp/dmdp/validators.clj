(ns dmdp.dmdp.validators
  (:require [bouncer.core :as bouncer]
            [bouncer.validators :as validators]
            [dmdp.db.core :as db]))

(defn is-user-unique? [email]
  (let [user-unique (first (db/get-user-by-email email))]
    (do (println (str user-unique))
        (= nil
         user-unique))))

(defn user-can-login? [email password]
  (println (str email " " password))
  (let [user (first (db/get-user-by-email {:email email}))]
    (and (not= user nil)
         (= (:pass user) password))))

; -- validators

(defn validate-login [params]
  (and (bouncer/valid?
         params
         :email validators/required
         :password validators/required)
       (user-can-login? (:email params) (:password params))))

(defn validate-registration [params]
  (and (bouncer/valid?
         params
         :first_name validators/required
         :last_name validators/required
         :email validators/required
         :password validators/required)
       (is-user-unique? params)))

(defn validate-edition [params]
  (bouncer/valid?
   params
   :first_name validators/required
   :last_name validators/required
   :email validators/required))

