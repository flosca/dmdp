(ns clojuredb.db
    (:require [taoensso.nippy :as nippy])
    (:use     [clojuredb.utils]))

(defn reload [] (use 'clojuredb.db :reload)) ;; for simplified developing in repl

(def META-TABLE-BYTES-LENGTH 9)
(def PAGE-DESCRIPTOR-BYTES-LENGTH 36)

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
     :index-descriptors []
     :page-descriptors []})

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
  {:page-id (Integer/valueOf page-id)
   :offset  (Integer/valueOf offset)})

(defn generate-page
  ([table-id page-id]
    {:table-id table-id
     :page-id page-id
     :records []})
  ([table-id page-id records]
    {:table-id table-id
     :page-id page-id
     :records records}))

(defn generate-record
  ([]
  {:fields []})
  ([fields]
  {:fields fields}))

(defn generate-field
 [attribute-id value]
  {:attribute-id attribute-id
   :value value})

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

(defn read-page
  [db-title table-id page-id]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
  (let [header (get-header db-title)
        page-descriptor (->> header
                             :table-descriptors
                             (filter #(= table-id (:table-id %)))
                             first
                             :page-descriptors
                             (filter #(= page-id (:page-id %)))
                             first)
        offset (:offset page-descriptor)
        buf (byte-array PAGE-DESCRIPTOR-BYTES-LENGTH)]
    (.seek raf offset)
    (.read raf buf)
    (nippy/thaw buf))))

(defn get-page
  [db-title table-id page-id]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
  (let [length (.length raf)]
  (if (> length PAGE-DESCRIPTOR-BYTES-LENGTH)
   (generate-page table-id page-id)
   (read-page db-title table-id page-id)))))



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

(defn get-page-descriptors
  [header table-name]
  (->> header
       :table-descriptors
       (filter #(= table-name (:table-name %)))
       first
       :page-descriptors))

(defn get-table-id
  [header table-name]
  (->> header
       :table-descriptors
       (filter #(= table-name (:table-name %)))
       first
       :table-id)))


(defn insert
  [db-title table-name record]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
  (let [header (get-header db-title)
        table-id (get-table-id header table-name)
        page-descriptors (get-page-descriptors header table-name))
        page-id (calc-record-hash db-title table-name record)]
 (if (some #(= page-id (:page-id %))
    ; if table already has page with such id
    (let [page (get-page db-title table-id page-id)]
    (write-page (generate-page (:table-id page) (:page-id page) (conj records record))))
    ; if table does not have the page



   (generate-header (conj (header :table-descriptors) ,,,)))

        ;page (get-page db-title table-id 0 #_(calc-record-hash record))]
