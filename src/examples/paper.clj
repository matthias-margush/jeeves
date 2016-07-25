(ns examples.paper
  (:require [jeeves.core :as jeeves]
            [paper.core :refer [paper] :as paper]))

(def p (paper {::paper/title     "Foo-Title"
               ::paper/accepted? false
               ::paper/stage     ::paper/submitted
               ::paper/author    "author/name"}))

(def title (::paper/title p))

(def author {::paper/name "author/name"
             ::paper/role ::paper/author})

(def reviewer {::paper/name "reviewer/name"
               ::paper/role ::paper/reviewer})

(def public {::paper/name "public/name"
             ::paper/role ::paper/public})

p
(jeeves/reveal (::paper/title p) reviewer)
(jeeves/reveal (::paper/title p) public)
(jeeves/reveal (::paper/title p) author)
(jeeves/reveal p reviewer)
(jeeves/reveal p author)
(jeeves/reveal p public)
