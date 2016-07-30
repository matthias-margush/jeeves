(ns examples.social
  (:require [clj-time.core :as time]
            [examples.social.core :as social]
            [jeeves.core :as jeeves :refer [let-sensitive]]))

(def joe (social/profile #:social{:name "joe" :yob 1966 :friends ["bob" "tom"]}))
(def moe (social/profile #:social{:name "moe" :yob 1965 :friends ["tim" "bob"]}))
(def bob (social/profile #:social{:name "bob" :yob 1955 :friends ["joe" "moe" "tom"]}))
(def tom (social/profile #:social{:name "tom" :yob 1955 :friends ["joe" "bob"]}))

(jeeves/reveal joe joe)
(jeeves/reveal joe moe)
(jeeves/reveal moe joe)
(jeeves/reveal (:social/name moe) moe)
;
(def age-diff (jeeves/sensitive-fn social/age-diff))
(def joe-v-moe (age-diff (:social/yob joe) (:social/yob moe)))

(jeeves/reveal joe-v-moe joe)
(jeeves/reveal joe-v-moe moe)
(jeeves/reveal joe-v-moe bob)
(jeeves/reveal joe-v-moe tom)

(def joe-v-bob
  (let-sensitive [j (:social/yob joe)
                  m (:social/yob bob)]
    (- j m)))

joe-v-bob
(jeeves/reveal joe-v-bob tom)
(jeeves/reveal joe-v-bob moe)
