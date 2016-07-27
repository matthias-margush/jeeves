(ns examples.paper.core
  (:require examples.paper.policies
            [jeeves.core :as jeeves]))

(defn paper [p] (jeeves/sensitive p))

(defn credits [title author]
  (str title "-" author))
