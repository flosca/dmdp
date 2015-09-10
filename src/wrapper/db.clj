(ns wrapper.db
  (:require [clojure.java.jdbc :refer :all]))



(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "db/base_sample.db"})

