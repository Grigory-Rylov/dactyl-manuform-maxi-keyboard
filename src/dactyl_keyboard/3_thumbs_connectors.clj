(ns dactyl-keyboard.3-thumbs-connectors
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

;tl -> m
(def three-thumb-connectors
  (union
   (triangle-hulls    ; top two
    (thumb-m-place web-post-tr)
    (thumb-m-place web-post-br)
    (thumb-r-place thumb-post-tl)
    (thumb-r-place thumb-post-bl))

   (triangle-hulls    ; top two to the middle two, starting on the left
    (thumb-m-place web-post-tl)
    (thumb-l-place web-post-tr)
    (thumb-m-place web-post-bl)
    (thumb-l-place web-post-br))

   (triangle-hulls    ; top two to the main keyboard, starting on the left
    (thumb-m-place web-post-tl)
    (key-place 0 cornerrow web-post-bl)
    (thumb-m-place web-post-tr)
    (key-place 0 cornerrow web-post-br)
    (thumb-r-place thumb-post-tl)
    (key-place 1 cornerrow web-post-bl)
    (thumb-r-place thumb-post-tr)
    (key-place 1 cornerrow web-post-br))))
