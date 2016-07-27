(ns examples.social.core
  (:require examples.social.policies
            [jeeves.core :as jeeves]))

(defn profile [p] (jeeves/sensitive p))

(defn age-diff [age1 age2]
  (- age1 age2))
