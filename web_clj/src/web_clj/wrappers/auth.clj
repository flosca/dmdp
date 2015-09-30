(ns web_clj.wrappers.auth
  (:require [web_clj.templator :as templator]))


(defn access-denied []
  {:status 403
   :body (templator/render-template "errors/403" {})})

(defn authorized [request]
  (println (str (:session request)))
  (= #_(rand-int 2) 1 1))

(defn wrap-auth [handler]
  (fn [request]
    (if (authorized request)
      (handler request)
      (access-denied))))

; Session example:
;
;   {"session-key": "23456",
;    "args": {"auth": true, "admin": false}}
;
;
;
;

#_(defn session-update [kv update]
    (++ (kv update)))

#_(session-update {} {:args {:auth true}})

;@sessions
