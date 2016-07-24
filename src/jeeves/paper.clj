(ns jeeves.paper
  (:require [jeeves.core :as jeeves]))

(defn public? [paper]
  (= :public (:stage paper)))

(defn accepted? [paper]
  (:accepted? paper))

(defmethod jeeves/policy ::title [field]
  (reify jeeves/Policy
    (level [this paper viewer]
      (cond
        (and (accepted? paper ) (public? paper))
        :high

        (= (::name viewer) (::author paper))
        :high

        (= (::role viewer) ::reviewer)
        :high

        (= (::role viewer) ::pc)
        :high

        :else
        :low))
    (scrub [this value level]
      (case level
        :high value
        ::scrubbed-title))))

(defmethod jeeves/policy ::accepted? [field]
  (reify jeeves/Policy
    (level [this paper viewer]
      (cond
        (= (::role viewer) ::reviewer)
        :high

        (= (::role viewer) ::pc)
        :high

        (= (::stage paper) ::public)
        :high

        :else
        :low))
    (scrub [this value level]
      (case level
        :high value
        ::scrubbed-accepted?))))

(defmethod jeeves/policy ::author [field]
  (reify jeeves/Policy
    (level [this paper viewer]
      (cond
        (= (::name viewer) (::author paper))
        :high

        (and (accepted? paper) (public? paper))
        :high

        :else
        :low))
    (scrub [this value level]
      (case level
        :high value
        ::scrubbed-author))))

(defn paper [p] (jeeves/sensitive p))

(def p (paper {::title "Foo-Title" ::accepted? true}))
(def title (::title p))
(def author {::name "author/name" ::role ::author})
(def reviewer {::name "reviewer/name" ::role ::reviewer})
(def public {::name "public/name" ::role ::public})
(jeeves/level (jeeves/policy ::title) p public)

(jeeves/reveal (::title p) reviewer)
(jeeves/reveal (::title p) public)
(jeeves/reveal p reviewer)
(jeeves/reveal p public)
