(ns dmdp.crawler.time
  (:require [clj-time.core :as t]
            [clj-time.format :as f]))

(def formatted-time (f/formatter "yyyy-MM-dd"))

(defn current-time []
(f/unparse formatted-time (t/now)))
