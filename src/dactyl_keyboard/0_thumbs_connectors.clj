(ns dactyl-keyboard.0-thumbs-connectors
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.connectors-common :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.config :refer :all]))


(def external-4-thumbs-connectors
  (union
   (triangle-hulls    ; top left
    (thumb-top-place web-post-tl)
    (thumb-top-place web-post-bl)
    (thumb-left-place thumb-post-tr)
    (thumb-left-place thumb-post-br)
    )
   (triangle-hulls    ; bottom right
    (thumb-bottom-place web-post-tr)
    (thumb-bottom-place web-post-br)
    (thumb-right-place thumb-post-tl)
    (thumb-right-place thumb-post-bl)
    )
   (triangle-hulls ;left bottom
    (thumb-bottom-place web-post-tl)
    (thumb-bottom-place web-post-tr)
    (thumb-left-place thumb-post-bl)
    (thumb-left-place thumb-post-br))
   (triangle-hulls ; top right
    (thumb-top-place web-post-bl)
    (thumb-top-place web-post-br)
    (thumb-right-place thumb-post-tl)
    (thumb-right-place thumb-post-tr))
   (triangle-hulls ; center
    (thumb-top-place web-post-bl)
    (thumb-left-place web-post-br)
    (thumb-right-place thumb-post-tl)
    (thumb-bottom-place thumb-post-tr)
    )
   ))
