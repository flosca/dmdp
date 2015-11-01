(ns dmdp.layout
  (:require [selmer.parser :as parser]
            [selmer.filters :as filters]
            [markdown.core :refer [md-to-html-string]]
            [ring.util.http-response :refer [content-type ok]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [environ.core :refer [env]]
            [clj-time.core :as t]))

(declare ^:dynamic *identity*)
(declare ^:dynamic *app-context*)
(parser/set-resource-path!  (clojure.java.io/resource "templates"))
(parser/add-tag! :csrf-field (fn [_ _] (anti-forgery-field)))
(filters/add-filter! :markdown (fn [content] [:safe (md-to-html-string content)]))

(filters/add-filter! :from-today
       (fn [date]
          (let [interval (t/in-minutes (t/interval (org.joda.time.DateTime. date) (t/now)))]
            (cond
             (<= interval 60) (str interval "minutes ago")
             (<= interval (* 60 24)) (str (quot interval 60) " hours ago")
             (<= interval (* 60 24 30)) (str (quot interval (* 60 24)) " days ago")
             (<= interval (* 60 24 30 12)) (str (quot interval (* 60 24 30)) " months ago")
             (<= interval (* 60 24 30 12 20)) (str (quot interval (* 60 24 30 12)) " years ago")
             :else "very very long ago"))))



(defn render
  "renders the HTML template located relative to resources/templates"
  [template & [params]]
  (content-type
    (ok
      (parser/render-file
        template
        (assoc params
          :page template
          :dev (env :dev)
          :csrf-token *anti-forgery-token*
          :servlet-context *app-context*)))
    "text/html; charset=utf-8"))

(defn error-page
  "error-details should be a map containing the following keys:
   :status - error status
   :title - error title (optional)
   :message - detailed error message (optional)

   returns a response map with the error page as the body
   and the status specified by the status key"
  [error-details]
  {:status  (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (parser/render-file "error.html" error-details)})
