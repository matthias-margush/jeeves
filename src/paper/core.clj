(ns paper.core
  (:require [jeeves.core :as jeeves]
            [paper.policies]))

(defn paper [p] (jeeves/sensitive p))
