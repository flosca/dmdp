(ns clojuredb.core
  (:require [clojure.java.io  :as io]
            [clojure.string   :as s]
            [b-plus-tree.io   :as bt-io]
            [b-plus-tree.core :as bt-core])
  (:import  [java.io RandomAccessFile]))

(defn reload [] (use 'clojuredb.core :reload)) ;; for simplified developing in repl

(defn title->file
  [title]
  (str "tables/" title ".db"))

(defn string->ascii
  [s]
    (map int s))

(defn ascii->string
  [code-vec]
    (apply str (map char code-vec)))

(defn write-symbol
  [file symbol offset]
    (doto (RandomAccessFile. file "rw")
          (.seek offset)
          (.write symbol)
          (.close)))

(defn write-string
  [file string start-offset]
  (loop [codes (string->ascii string)
         i start-offset]
   (if (empty? codes) nil
    (do
      (write-symbol file (first codes) i)
      (recur (rest codes) (inc i))))))

(defn delete-symbol
  [file offset]
    (doto (RandomAccessFile. file "rw")
          (.seek offset)
          (.write 0) ; setting NULL symbol to byte
          (.close)))

;; Databases functional

(defn initialize-database
"Creates a single file containing all the information about created database."
  [title]
 (let [file (title->file title)]
 (if (.exists (io/as-file file))
 (println "File's already exists.")
 (do
  (io/make-parents file)
  (spit file "")))))

(defn delete-database
  "Drops a file if exists."
  [title]
 (let [file (title->file title)]
 (if (.exists (io/as-file file))
 (io/delete-file file)
 (println "File's not exists yet."))))

 ;; Tables functional
(defn add-table
  "Adds table with fields to database.
   Fields are strings in vector."
  [db-title table-title fields]
(do
  (write-symbol (title->file db-title))))
