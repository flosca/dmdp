(ns web_clj.templator
  (:use compojure.core)
  (:require [clostache.parser :as clostache]
            ))

(defn read-template [template-name]
  (println (str "views/" template-name ".mustache"))
  (slurp (clojure.java.io/resource
          (str "views/" template-name ".mustache"))))

(defn render-template [template-file params]
  (clostache/render (read-template template-file) params))
