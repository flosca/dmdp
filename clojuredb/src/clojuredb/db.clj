(ns clojuredb.db
    (:require [taoensso.nippy :as nippy])
    (:use     [clojuredb.utils]))

(defn reload [] (use 'clojuredb.db :reload)) ;; for simplified developing in repl

(def META-TABLE-BYTES-LENGTH 9)

(defn values->data
  [table-id table-title]
  (nippy/freeze {:table-id table-id
                 :table-title table-title}))


(defn read-header-size
  "Returns the size of this shit"
  [db-title]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
     (.seek raf 0)
     (let [buf (byte-array META-TABLE-BYTES-LENGTH)
           _ (.read raf buf)]
       (nippy/thaw buf))))

(defn generate-header
  ([]
   {:table-descriptors []})
  ([table-descriptors]
   {:table-descriptors table-descriptors}))

(defn generate-table-descriptor
  [table-id table-name attributes]
    {:table-id table-id
     :table-name table-name
     :attributes attributes
     :index-descriptors []})

(defn generate-attribute
  [a-id a-name a-flags type]
  {:a-id a-id
   :a-name a-name
   :a-flags a-flags
   :type type})

(defn generate-index-descriptor
  []
  {:attribute-desciptors []})

(defn generate-attribute-descriptor
  [id offset]
  {:id id
   :idx-offset offset})

(defn generate-page-descriptor
  [page-id offset]
  {:page-id page-id
   :offset offset})

(defn generate-page
  ([table-id page-id records]
    {:table-id table-id
     :page-id page-id
     :records records})
  ([table-id page-id]
    {:table-id table-id
     :page-id page-id
     :records []}))

(defn read-header
   [db-title]
   (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
   (.seek raf META-TABLE-BYTES-LENGTH)
   (let [buf (byte-array (read-header-size db-title))
         _ (.read raf buf)]
     (nippy/thaw buf))))

(defn get-header
  [db-title]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
  (.seek raf 0)
  (let [length (.length raf)]
  (if (< length META-TABLE-BYTES-LENGTH)
   (generate-header)
   (read-header db-title)))))


(defn add-table
  [db-title table-name attributes]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
   (.seek raf 0)
   ; TODO: recalc table-id
   ; TODO: shift File
   ; TODO: unique table names
     (let [header (get-header db-title)
           table-descriptor (generate-table-descriptor (count (header :table-descriptors)) table-name attributes)
           data (nippy/freeze (generate-header (conj (header :table-descriptors) table-descriptor)))
           size (nippy/freeze (count data))]
           (println table-descriptor)
      (.write raf size)
      (.write raf data))))

(defn insert
  [db-title table-name record]
  )

#_(defn read-database-meta
  [db-title]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
     (.seek raf 0)
     (.read raf (values->data table-id table-title))))
