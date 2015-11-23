(ns dmdp.dbms.utils
  (:require [clojure.java.io  :as io]))



(defn title->file
[title]
(str "tables/" title ".db"))

(defn initialize-database
"Creates a single file containing all the information about created database."
[title]
(let [file (title->file title)]
(if (.exists (io/as-file file))
(println "File already exists.")
(do
(io/make-parents file)
(spit file "")))))

(defn delete-database
"Drops a file if exists."
[title]
(let [file (title->file title)]
(if (.exists (io/as-file file))
(io/delete-file file)
(println "File does not exist yet."))))

(defn like [value w-here]
  (not (nil? (re-matches (re-pattern (str "(?i)" ".*" value ".*")) w-here))))
