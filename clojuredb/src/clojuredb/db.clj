(ns clojuredb.db
    (:require [taoensso.nippy :as nippy])
    (:use     [clojuredb.utils]))

(defn reload [] (use 'clojuredb.db :reload)) ;; for simplified developing in repl

; ---- BYTES LENGTH FOR DIFFERENT DATA TYPES
(def META-TABLE-BYTES-LENGTH 9)
(def PAGE-DESCRIPTOR-BYTES-LENGTH 36)
; ------------------------------------------
; ---- BIT FLAG CONSTANTS
(def FLAG-PRIMARY-KEY 1)
; ------------------------------------------
; ---- OTHER CONSTANTS
(def PAGES-PER-TABLE 37)
;-------------------------------------------

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
    {:table-id (Integer/valueOf table-id)
     :table-name table-name
     :attributes attributes
     :index-descriptors []
     :page-descriptors []})

(defn generate-attribute
  [a-id a-name a-flags type]
  {:a-id (Integer/valueOf a-id)
   :a-name a-name
   :a-flags (Integer/valueOf a-flags)
   :type (Integer/valueOf type)})

(defn generate-index-descriptor
  []
  {:attribute-desciptors []})

(defn generate-attribute-descriptor
  [id offset]
  {:id (Integer/valueOf id)
   :idx-offset (Integer/valueOf offset)})

(defn generate-page-descriptor
  [page-id offset]
  {:page-id (Integer/valueOf page-id)
   :offset  (Integer/valueOf offset)})

(defn generate-page
  ([table-id page-id records]
    {:table-id (Integer/valueOf table-id)
     :page-id (Integer/valueOf page-id)
     :records records})
  ([table-id page-id]
    {:table-id (Integer/valueOf table-id)
     :page-id (Integer/valueOf page-id)
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
  (if (< PAGE-DESCRIPTOR-BYTES-LENGTH length)
   (generate-page table-id page-id)
   (read-page db-title table-id page-id)))))

(defn get-attributes
  [db-title table-name]
  (let [table-descriptors (:table-descriptors (get-header db-title))
        attributes (filter #(= table-name (:table-name %)) table-descriptors)]
        attributes))

(defn calc-record-hash
  [db-title table-name record]
  (loop [i 0
         hash-acc 0
         attributes (get-attributes db-title table-name)]
         (if (< i (count record))
          (recur (inc i) (let [current-attribute (nth attributes i)
                               current-attribute-flags (:flags current-attribute)
                               current-field (nth record i)
                               current-field-value-hash (hash (:value current-field))
                            (if (= (bit-and (current-attribute-flags FLAG-PRIMARY-KEY)) 1)
                                (+ hash-acc current-field-value-hash) hash-acc)) attributes)
          (Math/abs (quot (Math/abs hash-acc) PAGES-PER-TABLE)))))

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
