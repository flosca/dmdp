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
(def PAGE-SIZE 1024)
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

(defn generate-record
  ([]
  {:fields []})
  ([fields]
  {:fields fields}))

(defn generate-field
 [attribute-id value]
  {:attribute-id (Integer/valueOf attribute-id)
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
                               current-field-value-hash (hash (:value current-field))]
                            (if (= (bit-and (current-attribute-flags FLAG-PRIMARY-KEY)) 1)
                                (+ hash-acc current-field-value-hash) hash-acc)) attributes)
          (Math/abs (quot (Math/abs hash-acc) PAGES-PER-TABLE)))))

(defn write-header
  [db-title header]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
   (.seek raf 0)
   (let [;(read-header-size db-title)
         data (nippy/freeze header)
         size (count data)]
  ; (shift-file (title->file db-title) (+ META-TABLE-BYTES-LENGTH old-size) (- size old-size))
   (.write raf (nippy/freeze size))
   (.write raf data)
   (if (<= (.length raf) PAGE-SIZE) ;TODO header does not have to be more than 1,0Kb
      (.setLength raf PAGE-SIZE)))))

(defn write-page
  [db-title page]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]

(defn add-table
  [db-title table-name attributes]
   ; TODO: recalc table-id
   ; TODO: shift File
   ; TODO: unique table names
     (let [header (get-header db-title)
           table-descriptor (generate-table-descriptor (count (header :table-descriptors)) table-name attributes)]
      (write-header db-title (generate-header (conj (header :table-descriptors) table-descriptor)))))

(defn get-page-descriptors
  [header table-name]
  (->> header
       :table-descriptors
       (filter #(= table-name (:table-name %)))
       first
       :page-descriptors))

(defn get-table-descriptor
  [header table-name]
  (->> header
       :table-descriptors
       (filter #(= table-name (:table-name %)))
       first))

(defn get-table-id
  [header table-name]
  (->> header
       :table-descriptors
       (filter #(= table-name (:table-name %)))
       first
       :table-id))

(defn write-page-descriptor
  [db-title table-id page-id]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
  (let [header (get-header db-title)
        page-descriptor (first (filter #(= page-id (:page-id %)) (get-page-descriptors header table-name)))
        table-descriptor (generate-table-descriptor )
        offset (.length raf)]
        (if (nil? page-descriptor)
        (get-table-descriptor )
        (generate-page-descriptor page-id offset))

(comment
(defn insert
  [db-title table-name record]
  (with-open [raf (new java.io.RandomAccessFile (title->file db-title) "rwd")]
  (let [header (get-header db-title)
        table-id (get-table-id header table-name)
        page-descriptors (get-page-descriptors header table-name)
        page-id (calc-record-hash db-title table-name record)]
 (if (some #(= page-id (:page-id %))
    ; if table already has page with such id
    (let [page (get-page db-title table-id page-id)]
    (write-page-descriptor db-title table-id page-id)
    (write-page (generate-page (:table-id page) (:page-id page) (conj records record))))
    ; if table does not have the page

   (generate-header (conj (header :table-descriptors) ,,,))))))))

        ;page (get-page db-title table-id 0 #_(calc-record-hash record))]
