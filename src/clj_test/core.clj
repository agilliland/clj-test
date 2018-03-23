(ns clj-test.core
  (:require [clj-http.client :as http])
  (:gen-class))

(def comic-url-base "http://www.questionablecontent.net/comics/%d.png")


(defn save-file [comic-number bytes]
  (let [file-path (str "/tmp/" comic-number ".png")]
    (with-open [os (clojure.java.io/output-stream file-path)]
      (.write os bytes))
    file-path))


(defn comic-url [{:keys [comic-number] :as comic}]
  (assoc comic :comic-url (format comic-url-base comic-number)))

(defn fetch-comic [{:keys [comic-url] :as comic}]
  (let [res (http/get comic-url {:as :byte-array})]
    (-> comic
        (assoc :success (= (:status res) 200))
        (assoc :image (:body res)))))

(defn save-comic [{:keys [success, comic-number, image] :as comic}]
  (if-not success
    comic
    (assoc comic :save-location (save-file comic-number image))))

(defn download-comics [count]
  (->> (range 1 count)
       (map #(assoc {} :comic-number %))
       (map comic-url)
       (map fetch-comic)
       (map save-comic)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (download-comics 3))

