(ns jeeves.core
  (:require [clojure.spec :as s]
            [clojure.algo.generic.functor :refer [fmap]]))

(defprotocol Policy
  (level [this ctx viewer])
  (scrub [this value level]))

(defmulti policy "" identity)

(defprotocol Revealing
  (reveal [this viewer]))

(deftype Sensitive [ctx field value]
  Revealing
  (reveal [this viewer]
    (let [p (policy field)
          l (level p ctx viewer)
          s (scrub p value l)]
      s))
  Object
  (toString [this] (pr-str {field ::scrubbed})))

(defn sensitive
  ([ctx]
   (into {} (for [[field value] ctx]
              [field (sensitive ctx field value)])))
  ([ctx field value]
   (if (map? value)
     (sensitive value)
     (Sensitive. ctx field value))))
