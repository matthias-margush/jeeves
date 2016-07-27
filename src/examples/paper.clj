(ns examples.paper
  (:require [examples.paper.core :as paper :refer [paper]]
            [jeeves.core :as jeeves]))

(def p (paper {:paper/title     "Foo-Title"
               :paper/accepted? false
               :paper/stage     :stage/submitted
               :paper/author    "author/name"
               :paper/content   "Lorem Ipsum"}))

(def title (:paper/title p))

(def author {:participant/name "author/name"
             :participant/role :role/author})

(def reviewer {:participant/name "reviewer/name"
               :participant/role :role/reviewer})

(def public {:participant/name "public/name"
             :participant/role :role/public})

p
(jeeves/reveal (:paper/title p) reviewer)
(jeeves/reveal (:paper/title p) public)
(jeeves/reveal (:paper/title p) author)
(jeeves/reveal p reviewer)
(jeeves/reveal p author)
(jeeves/reveal p public)

(def credit (jeeves/sensitive-fn paper/credits))
(def c (credit (:paper/title p) (:paper/author p)))

c
(jeeves/reveal c reviewer)
(jeeves/reveal c public)
(jeeves/reveal c author)
