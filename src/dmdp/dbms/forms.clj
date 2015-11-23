(ns dmdp.dbms.forms)

(defn generate-header
  ([]
   {:table-descriptors []})
  ([table-descriptors]
   {:table-descriptors table-descriptors}))

(defn generate-table-descriptor
  ([table-id table-name attributes]
    {:table-id (Integer/valueOf table-id)
     :table-name table-name
     :attributes attributes
     :index-descriptors []
     :page-descriptors []})
  ([table-id table-name attributes index-descriptors page-descriptors]
    {:table-id (Integer/valueOf table-id)
     :table-name table-name
     :attributes attributes
     :index-descriptors index-descriptors
     :page-descriptors page-descriptors}))

(defn generate-attribute
  [a-id a-name a-flags type]
  {:id (Integer/valueOf a-id)
   :name a-name
   :flags (Integer/valueOf a-flags)
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
  "Records are represented as sets, in order to avoid insert duplicates"
  ([table-id page-id records]
    {:table-id (Integer/valueOf table-id)
     :page-id (Integer/valueOf page-id)
     :records records})
  ([table-id page-id]
    {:table-id (Integer/valueOf table-id)
     :page-id (Integer/valueOf page-id)
     :records #{}}))

(defn generate-record
  ([]
  {:fields []})
  ([fields]
  {:fields fields}))

(defn generate-field
 [attribute-id value]
  {:attribute-id (Integer/valueOf attribute-id)
   :value value})
