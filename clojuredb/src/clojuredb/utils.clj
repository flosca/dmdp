(ns clojuredb.utils
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

(defn shift-file
  [filename pos-start count]
    (with-open [raf (new java.io.RandomAccessFile filename "rwd")]
      (loop [i (dec (.length raf))
             pos-read (dec (.length raf))
             pos-write (dec (+ (.length raf) count))]
            (if (>= i pos-start)
                (do
                  (printf "pr: %s, pw: %s; " pos-read pos-write)
                  (let [current-byte (do (.seek raf pos-read)
                                         (.read raf))]
                    (.seek raf pos-write)
                    (.write raf current-byte))
                  (recur (dec i) (dec pos-read) (dec pos-write)))))
      (.setLength raf (+ (.length raf) (if (<= count 0) count 0) ))))
