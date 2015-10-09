(ns dmdp.dmdp.validators
  (:require [bouncer.core :as bouncer]
            [bouncer.validators :as validators]))

(defn validate-login [params]
  (first
     (bouncer/validate
        params
        :username validators/required
        :password validators/required)))

