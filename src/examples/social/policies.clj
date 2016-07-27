(ns examples.social.policies
  (:require [jeeves.core :refer [defpolicy]]
            [jeeves.core :as jeeves]))

(defpolicy :social/name
  (level [profile viewer]
    (cond
      (= (:social/name profile) (:social/name viewer))
      jeeves/high

      (some #(= % (:social/name viewer)) (:social/friends profile))
      jeeves/high

      :else
      jeeves/low)))

(defpolicy :social/yob
  (level [profile viewer]
    (cond
      (= (:social/name profile) (:social/name viewer))
      jeeves/high

      (some #(= % (:social/name viewer)) (:social/friends profile))
      jeeves/high

      :else
      jeeves/low)))
