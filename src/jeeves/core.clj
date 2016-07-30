(ns jeeves.core
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [jeeves.core :as jeeves])
  (:import clojure.lang.Associative))

(def high 1)
(def low 0)

(defprotocol Policy
  (level [this ctx viewer] "")
  (scrub [this value level] ""))

(defprotocol Revealing
  ""
  (reveal [this viewer]))

(declare unveil)

(deftype Sensitive [ctx policy value]
  Revealing
  (reveal [this viewer]
    (->> (unveil viewer)
         (level policy ctx)
         (scrub policy value)))
  Object
  (toString [this] (pr-str ::scrubbed)))

(defn unveil [v]
  (cond
    (instance? Sensitive v)
    (.value v)

    (map? v)
    (fmap unveil v)

    :default
    v))

(defmethod print-method Sensitive [v ^java.io.Writer w]
  (.write w "<<-scrubbed->>"))

(defmulti tagged-level "" (fn [tag ctx viewer] tag))

(defmethod tagged-level :default
  [tag ctx viewer]
  high)

(defmulti tagged-scrub "" (fn [tag value level] tag))

(defmethod tagged-scrub :default
  [tag value level]
  (if (< level high)
    ::scrubbed
    value))

(defrecord TaggedPolicy [tag]
  Policy
  (level [this ctx viewer]
    (tagged-level tag ctx viewer))
  (scrub [this value level]
    (tagged-scrub tag value level)))

(defn tagged-policy
  ""
  [tag]
  (TaggedPolicy. tag))

(defn sensitive
  ([ctx]
   (sensitive ctx ctx))
  ([value ctx]
   (into {} (for [[k v] value]
              [k (if (map? v)
                   (sensitive v ctx)
                   (Sensitive. ctx (tagged-policy k) v))]))))

(extend-type Associative
  Revealing
  (reveal [ctx viewer]
    (fmap #(reveal % viewer) ctx)))

(deftype CompositePolicy [values]
  Policy
  (level [this ctx viewer]
    (let [levels (for [v values] (level (.policy v) (.ctx v) viewer))]
      (apply min levels)))
  (scrub [this value level]
    (if (= high level)
      value
      ::scrubbed)))

(defn calc
  ""
  [f & params]
  (let [unveiled (map #(.value %) params)
        result (apply f unveiled)]
    (Sensitive. {} (CompositePolicy. params) result)))

(defn sensitive-fn
  ""
  [f]
  (fn [& params] (apply calc f params)))

(defn sens [ctx policy v]
  (Sensitive. ctx policy v))

(defn composite-policy [vs]
  (CompositePolicy. vs))

(defmacro let-sensitive
  ""
  [let-clauses & body]
  `(let ~(vec (mapcat (fn [[k v]] [k `(.value ~v)]) (partition 2 let-clauses)))
     (let [result# (do ~@body)]
       (jeeves.core/sens {} (jeeves/composite-policy ~(vec (map second (partition 2 let-clauses)))) result#))))

(defmacro defpolicy
  ""
  ([tag [level [ctx viewer] & body]]
   `(defmethod jeeves.core/tagged-level ~tag
      [_# ~ctx ~viewer]
      ~@body))
  ([tag
   [level [ctx viewer] & level-body]
   [scrub [value lvl] & scrub-body]]
   `(do (defmethod jeeves.core/tagged-level ~tag
          [_# ~ctx ~viewer]
          ~@level-body)
        (defmethod jeeves.core/tagged-scrub ~tag
          [_# ~value ~lvl]
          ~@scrub-body))))
