(ns examples.social
  (:require [clj-time.core :as time]
            [examples.social.core :as social]
            [jeeves.core :as jeeves]))

(def joe (social/profile #:social{:name "joe" :yob 1966 :friends ["bob"]}))
(def moe (social/profile #:social{:name "moe" :yob 1965 :friends ["joe" "bob"]}))
(def bob (social/profile #:social{:name "bob" :yob 1955 :friends ["joe" "moe"]}))
(def tim (social/profile #:social{:name "tim" :yob 1955 :friends []}))

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
(jeeves/reveal joe-v-moe tim)
