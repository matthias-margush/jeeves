(ns paper.policies
  (:require [jeeves.core :as jeeves]))

(defn public? [paper]
  (= :public (:stage paper)))

(defn accepted? [paper]
  (:accepted? paper))

(defmethod jeeves/policy :paper.core/title [field]
  (reify jeeves/Policy
    (level [this paper viewer]
      (cond
        (and (accepted? paper ) (public? paper))
        ::jeeves/high

        (= (:paper.core/name viewer) (:paper.core/author paper))
        ::jeeves/high

        (= (:paper.core/role viewer) :paper.core/reviewer)
        ::jeeves/high

        (= (:paper.core/role viewer) :paper.core/pc)
        ::jeeves/high

        :else
        ::jeeves/low))
    (scrub [this value level]
      (case level
        ::jeeves/high value
        ""))))

(defmethod jeeves/policy :paper.core/accepted? [field]
  (reify jeeves/Policy
    (level [this paper viewer]
      (cond
        (= (:paper.core/role viewer) :paper.core/reviewer)
        ::jeeves/high

        (= (:paper.core/role viewer) :paper.core/pc)
        ::jeeves/high

        (= (:paper.core/stage paper) :paper.core/public)
        ::jeeves/high

        :else
        ::jeeves/low))
    (scrub [this value level]
      (case level
        ::jeeves/high value
        ::unknown))))

(defmethod jeeves/policy :paper.core/author [field]
  (reify jeeves/Policy
    (level [this paper viewer]
      (cond
        (= (:paper.core/name viewer) (:paper.core/author paper))
        ::jeeves/high

        (and (accepted? paper) (public? paper))
        ::jeeves/high

        :else
        ::jeeves/low))
    (scrub [this value level]
      (case level
        ::jeeves/high value
        "Anonymized"))))
