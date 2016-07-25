(ns jeeves.core
  (:require [clojure.algo.generic.functor :refer [fmap]]))

(defprotocol Policy
  (level [this ctx viewer])
  (scrub [this value level]))

(defmulti policy "" identity)

(defmethod policy :default [field]
  (reify Policy
    (level [this ctx viewer] ::low)
    (scrub [this value level] ::scrubbed)))

(defprotocol Revealing
  (reveal [this viewer]))

(deftype SensitiveValue [ctx field value]
  Revealing
  (reveal [this viewer]
    (let [p (policy field)
          l (level p ctx viewer)
          s (scrub p value l)]
      s))
  Object
  (toString [this] (pr-str {field ::scrubbed})))

(declare sensitive)

(deftype SensitiveMap [m]
  Revealing
  (reveal [this viewer] (fmap #(reveal % viewer) m))

  clojure.lang.Associative
  (containsKey [this key] (contains? m key))
  (entryAt [this key] (get m key))
  (assoc [this key val] (SensitiveMap. (assoc m key (sensitive m key val))))

  clojure.lang.ILookup
  (valAt [this k] (get m k))
  (valAt [this k d] (get m k d))

  clojure.lang.IFn
  (invoke [this kw] (get m kw))

  clojure.lang.Seqable
  (seq [this] (seq m))

  Object
  (toString [this] (pr-str m)))

(defn sensitive
  ([ctx]
   (SensitiveMap.
    (into {} (for [[field value] ctx]
               [field (sensitive ctx field value)]))))
  ([ctx field value]
   (if (map? value)
     (SensitiveMap. value)
     (SensitiveValue. ctx field value))))
