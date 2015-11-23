(ns dmdp.dbms.utils
  (:require [clojure.java.io  :as io]))



(defn title->file
[title]
(str "tables/" title ".db"))

(defn exists?
  [title]
  (.exists (io/as-file (title->file title))))

(defn initialize-database
"Creates a single file containing all the information about created database."
[title]
(let [file (title->file title)]
(if (exists? title)
(do (println "File already exists.") nil)
(do
(io/make-parents file)
(spit file "")))))

(defn delete-database
"Drops a file if exists."
[title]
(let [file (title->file title)]
(if (exists? title)
(io/delete-file file)
(do (println "File does not exist yet.") nil))))

(defn like [value w-here]
  (not (nil? (re-matches (re-pattern (str "(?i)" ".*" value ".*")) w-here))))
