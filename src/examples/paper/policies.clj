(ns examples.paper.policies
  (:require [jeeves.core :refer [defpolicy] :as jeeves]))

(defn public? [paper]
  (= :stage/public (:paper/stage paper)))

(defn accepted? [paper]
  (:paper/accepted? paper))

(defpolicy :paper/title
  (level [paper viewer]
    (cond
      (and (accepted? paper ) (public? paper))
      jeeves/high

      (= (:participant/name viewer) (:paper/author paper))
      jeeves/high

      (= (:participant/role viewer) :role/reviewer)
      jeeves/high

      (= (:participant/role viewer) :role/pc)
      jeeves/high

      :else
      jeeves/low)))

(defpolicy :paper/title
  (level [paper viewer]
    (cond
      (and (accepted? paper ) (public? paper))
      jeeves/high

      (= (:participant/name viewer) (:paper/author paper))
      jeeves/high

      (= (:participant/role viewer) :role/reviewer)
      jeeves/high

      (= (:participant/role viewer) :role/pc)
      jeeves/high

      :else
      jeeves/low)))


(defpolicy :paper/accepted?
  (level [paper viewer]
    (cond
      (= (:participant/role viewer) :role/reviewer)
      jeeves/high

      (= (:participant/role viewer) :role/pc)
      jeeves/high

      (= (:participant/stage paper) :stage/public)
      jeeves/high

      :else
      jeeves/low)))

(defpolicy :paper/author
  (level [paper viewer]
    (cond
      (= (:participant/name viewer) (:paper/author paper))
      jeeves/high

      (and (accepted? paper) (public? paper))
      jeeves/high

      :else
      jeeves/low)))
