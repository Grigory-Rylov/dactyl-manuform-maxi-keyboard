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
  (let [tl web-post-tl-c
        tr web-post-tr-c
        bl web-post-bl-c
        br web-post-br-c]
    (union
     (triangle-hulls    ; top two
      (thumb-m-place tr)
      (thumb-m-place br)
      (thumb-r-place tl)
      (thumb-r-place bl))

     (triangle-hulls    ; top two to the middle two, starting on the left
      (thumb-m-place tl)
      (thumb-l-place tr)
      (thumb-m-place bl)
      (thumb-l-place br))

     (triangle-hulls    ; top two to the main keyboard, starting on the left
      (thumb-m-place tl)
      (key-place 0 cornerrow bl)
      (thumb-m-place tr)
      (key-place 0 cornerrow br)
      (thumb-r-place tl)
      (key-place 1 cornerrow bl)
      (thumb-r-place tr)
      (key-place 1 cornerrow br)))))
