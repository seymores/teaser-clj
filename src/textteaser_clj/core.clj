(ns textteaser-clj.core
  (:require [textteaser-clj.html :refer [process-html]]
            [textteaser-clj.parsing :as parsing]
            [textteaser-clj.scoring :refer [score-sentences top-x normalize]]
            [textteaser-clj.stopwords :refer [filter-stopwords-string
                                          filter-stopwords-wordmap
                                          filter-symbols]]
            [boilerpipe-clj.core :as boilerpipe]
            [clojure.string :as string]))

(defn- get-from-indices
  "Returns the values from a data structure corresponding to passed indices."
  [sentences indices]
  (for [i indices]
    (nth sentences i)))

(defn- merge-sentence-score-order
  [sentences scores]
  (for [i scores] { :order (key i) :sentence (nth sentences (key i)) :score (val i) }))

(defn summarize
  [title sentences]
  (let [words        (filter-symbols (mapcat parsing/tokenize sentences))
        lowercase    (map string/lower-case sentences)
        startmap     (frequencies (map string/lower-case words))
        wordcount    (count startmap)
        keyword-map  (->> (filter-stopwords-wordmap startmap)
                          (top-x 10)
                          (normalize wordcount))]
    (->> (filter-stopwords-string title)
         (score-sentences lowercase keyword-map wordcount)
         (top-x 5)
         (keys)
         (get-from-indices sentences)
         (string/join "  "))))

(defn summarize-with-score
  [title sentences]
  (let [words        (filter-symbols (mapcat parsing/tokenize sentences))
        lowercase    (map string/lower-case sentences)
        startmap     (frequencies (map string/lower-case words))
        wordcount    (count startmap)
        keyword-map  (->> (filter-stopwords-wordmap startmap)
                          (top-x 10)
                          (normalize wordcount))]
    (->> (filter-stopwords-string title)
         (score-sentences lowercase keyword-map wordcount)
         (top-x 4)
         (merge-sentence-score-order sentences)
         (sort-by :order))))

(defn summarize-url
  "Returns a five-sentence (max) summary of the given url."
  [url]
  (let [{:keys [title sentences]}  (process-html url)]
    (summarize title (parsing/get-sentences sentences))))

(defn summarize-text
  "Returns a five-sentence (max) summary of the given story and title."
  [title story]
  (let [sentences (parsing/get-sentences story)]
    (summarize title sentences)))

(defn summarize-url-with-score
  "Returns a five-sentence (max) summary of the given url."
  [url]
  (let [{:keys [title sentences]}  (process-html url)]
    (summarize-with-score title (parsing/get-sentences sentences))))

(defn summarize-text-with-score
  "Returns a five-sentence (max) summary of the given story and title."
  [title story]
  (let [sentences (parsing/get-sentences story)]
    (summarize-with-score title sentences)))
