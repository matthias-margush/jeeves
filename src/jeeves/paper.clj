(ns jeeves.paper
  (:require [clojure.spec :as s]
            [jeeves.core :as jeeves]))

(defn public? [paper]
  (= :public (:stage paper)))

(defn accepted? [paper]
  (:accepted? paper))

(defmethod jeeves/policy ::title [field]
  (reify jeeves/Policy
    (level [this paper viewer]
        (cond
          (and (accepted? paper ) (public? paper))
          :low

          (= (:name viewer) (:author paper))
          :low

          (= (:role viewer) :reviewer)
          :low

          (= (:role viewer) :pc)
          :low

          :else
          :high))
    (scrub [this value level]
      (case level
        :low value
        "Scrubbed ::title"))))

(defmethod jeeves/policy ::accepted? [field]
  (reify jeeves/Policy
    (level [this paper viewer]
      (cond
        (= (:role viewer) :reviewer)
        :low

        (= (:role viewer) :pc)
        :low

        (= (:stage paper) :public)
        :low

        :else
        :high))
    (scrub [this value level]
      (case level
        :low value
        "Scrubbed ::accepted?"))))

(defn paper [p] (jeeves/sensitive p))

(def p (paper {::title "Foo-Title" ::accepted? true}))
(def title (::title p))
(def author {::name "author/name" ::role ::author})
(def reviewer {::name "reviewer/name" ::role ::reviewer})
(def public {::name "public/name" ::role ::public})

(jeeves/reveal (::title p) reviewer)
(jeeves/reveal (::title p) public)
