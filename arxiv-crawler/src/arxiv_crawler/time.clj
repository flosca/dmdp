(ns arxiv-crawler.time
  (:require [clj-time.periodic :as p]
            [clj-time.core :as t]
            [clj-time.format :as f]))

(def formatted-time (f/formatter "yyyy-MM-dd"))


(defn generate-vector-of-dates
  []
  (map vector
       (map #(f/unparse formatted-time %)
            (take 100 (p/periodic-seq (t/date-time 2007 05 02) (t/months 1))))
       (map #(f/unparse formatted-time %)
            (take 100 (p/periodic-seq (t/date-time 2007 06 01) (t/months 1))))))


