(ns textteaser-clj.html
  (:require [textteaser-clj.parsing :as parsing]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :refer [as-url]]
            [org.httpkit.client :as http]
            [boilerpipe-clj.core :as boilerpipe]
            [clojure.string :refer [lower-case split]]))

(defn title-from-html
  "Returns the title from enlive html."
  [html]
  (first (map lower-case (html/select html [:title html/text-node]))))

;; (defn fetch-url
;;   "Grabs a given URI and returns enlive html of the page."
;;   [url]
;;   (html/html-resource (as-url url)))

;; (defn fetch-page
;;   [url]
;;   (let [body (http/get url {:as :stream})]
;;     (html/html-resource (:body @body))))

(defn sentences-from-html
  "Returns the sentences from enlive html."
  [html]
  (try
    (let [chunks (-> html
                     (html/at [:script] nil)
                     (html/at [:style] nil)
                     (html/select [:body html/text-node]))]
           (mapcat parsing/get-sentences chunks))
    (catch IllegalArgumentException e)))

(defn process-html
  "Returns a map with the title, words, and sentences from a given url."
  [url]
  (let [page (http/get url {:as :stream :keepalive 10000})
        body (:body @page)
        content (html/html-resource body)
        bodyText (:body @(http/get url)) ]
    {:title (title-from-html content)
     :sentences  (boilerpipe/get-text bodyText boilerpipe-clj.extractors/article-sentence-extractor)}))
