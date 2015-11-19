(ns clojuredb.core
(:require [clojure.java.io  :as io]
          [clojure.string   :as s]
          [b-plus-tree.io   :as bt-io]
          [b-plus-tree.core :as bt-core]
          [gloss.core       :refer :all]
          [gloss.io         :as g-io])
(:import  [java.io RandomAccessFile]))

;(defn reload [] (use 'clojuredb.core :reload)) ;; for simplified developing in repl

(defcodec attribute-descriptor
(ordered-map
  :attribute-id :byte
  :attribute (string :ascii :length 20)
  :type :byte
  :flags :byte))

(defcodec table-descriptor
(repeated
(ordered-map
  :table-id   :int32
  :table-title (string :ascii :length 20)
  :attributes (repeated attribute-descriptor))))

(defn table-meta-size
  []
  (byte-count
    (g-io/encode table-descriptor
      (repeat 1  {:table-id 0
                  :table-title ""
                  :attributes (repeat 1  {:attribute-id 0
                                          :attribute ""
                                          :type 0
                                          :flags 0})}))))


(defn title->file
[title]
(str "tables/" title ".db"))

(defn string->ascii
[s]
  (map int s))

(defn ascii->string
[code-vec]
  (apply str (map char code-vec)))

(def meta-delimiter (first (string->ascii "♠"))) ;; 9824

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

(defn delete-string
[file start-offset length]
    (loop [l length
           i start-offset]
     (if (zero? l) nil
      (do
        (delete-symbol file i)
        (recur (dec l) (inc i))))))


;; Databases functional

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

;; Tables functional
#_(defn table-descriptor-size
  []
  (byte-count
    (g-io/encode table-descriptor
      )))

(defn read-table-descriptor
  "Reads the header from the RandomAccessFile."
  [title]
  (with-open [raf (new java.io.RandomAccessFile (title->file title) "rwd")]
     (.seek raf 0) ; go to head of file
     (let [header-bytes (byte-array (table-meta-size))]
       (.readFully raf header-bytes)
       (println (g-io/decode table-descriptor header-bytes)))))

(defn read-node
  [offset title]
  (with-open [raf (new java.io.RandomAccessFile (title->file title) "rwd")]
          (.seek raf offset)
          (let [size (.readShort raf)
                node-bytes (byte-array size)]
            (.readFully raf node-bytes)
            (g-io/decode table-descriptor (g-io/to-byte-buffer node-bytes)))))

(defn add-table-1
[db-title table-id table-title attributes offset]
(let [file (title->file db-title)
      encoded-table (g-io/encode table-descriptor
                               [{:table-id     table-id
                                 :table-title  table-title
                                 :attributes   attributes}])
      size (byte-count encoded-table)]
   (with-open [raf (new java.io.RandomAccessFile file "rwd")]
   (doto raf
     (.seek offset)
     (.writeShort size)
     (.write (.array (g-io/contiguous encoded-table))))
   (.getFilePointer raf))))


(defn add-table
"Adds table with fields to database.
Fields are strings in vector."
[db-title table-title] ;fields]
(let [file (title->file db-title)
    header (g-io/encode table-descriptor
                               (conj (read-table-descriptor file) {:table-id     0
                                                             :table-title  table-title}))]
   (with-open [raf (new java.io.RandomAccessFile file "rwd")]
       (.write raf
               (.array (g-io/contiguous header))))))
