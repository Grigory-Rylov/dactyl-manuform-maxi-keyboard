(ns dactyl-keyboard.0-thumbs-case
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.case-common :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.connectors-common :refer :all]
            [dactyl-keyboard.config :refer :all]))


(def no-thumb-case-walls
  (union
   right-wall

   left-wall
   ; back wall
   (for [x (range 0 ncols)]
     (if (not= x 1) (key-wall-brace x 0 0 1 web-post-tl x 0 0 1 web-post-tr)))
   (for [x (range 1 ncols)]
     (if (not= x 2) (key-wall-brace x 0 0 1 web-post-tl (dec x) 0 0 1 web-post-tr)))
   (key-wall-brace 2 0 0 1 web-post-tl 2 0 -0.2 1 web-post-tr)
   (key-wall-brace 2 0 0.0 1 web-post-tl (dec 2) 0 -0.2 1 web-post-tr)
   (key-wall-brace 1 0 0 1 web-post-tl 1 0 -0.2 1 web-post-tr)


   ; front wall
   (key-wall-brace 3 lastrow 0 -1 web-post-bl 2 lastrow 0 -1 web-post-br)

   (for [x (range 0 ncols)]
     (key-wall-brace x cornerrow 0 -1 web-post-bl x cornerrow 0 -1 web-post-br))
   (for [x (range 1 ncols)]
     (key-wall-brace x cornerrow 0 -1 web-post-bl (dec x) cornerrow 0 -1 web-post-br))

   ; end union
   ))

(def external-thumb-offset 1.0)
(def external-4-thumbs-case-walls
  (union
   ; thumb walls
   (wall-brace thumb-bottom-place 0 (* external-thumb-offset -1) web-post-bl thumb-bottom-place 0 (* external-thumb-offset -1) thumb-post-br)
   (wall-brace thumb-middle-place 0 (* external-thumb-offset -1) web-post-bl thumb-middle-place 0 (* external-thumb-offset -1) thumb-post-br)
   (wall-brace thumb-middle-place external-thumb-offset 0 web-post-br thumb-middle-place external-thumb-offset 0 thumb-post-tr)
   (wall-brace thumb-top-place external-thumb-offset 0 web-post-br thumb-top-place external-thumb-offset 0 thumb-post-tr)
   (wall-brace thumb-top-place 0 external-thumb-offset web-post-tr thumb-top-place 0 external-thumb-offset thumb-post-tl)
;   (wall-brace thumb-left-place 0 external-thumb-offset web-post-tr thumb-left-place 0 external-thumb-offset thumb-post-tl)
;   (wall-brace thumb-left-place (* external-thumb-offset -1) 0 web-post-tl thumb-left-place (* external-thumb-offset -1) 0 thumb-post-bl)
   (wall-brace thumb-bottom-place (* external-thumb-offset -1) 0 web-post-tl thumb-bottom-place (* external-thumb-offset -1) 0 thumb-post-bl)

   ; thumb corners
   (wall-brace thumb-bottom-place (* external-thumb-offset -1) 0 web-post-bl thumb-bottom-place 0 (* external-thumb-offset -1) web-post-bl)

;   (wall-brace thumb-left-place (* external-thumb-offset -1) 0 web-post-tl thumb-left-place 0 external-thumb-offset web-post-tl)
   (wall-brace thumb-middle-place external-thumb-offset 0 web-post-br thumb-middle-place 0 (* external-thumb-offset -1) web-post-br)

   (wall-brace thumb-top-place external-thumb-offset 0 web-post-tr thumb-top-place 0 external-thumb-offset web-post-tr)

   ; thumb tweeners
;   (wall-brace thumb-top-place 0 external-thumb-offset web-post-tl thumb-left-place 0 external-thumb-offset web-post-tr)
   (wall-brace thumb-middle-place 0 (* external-thumb-offset -1) web-post-bl thumb-bottom-place 0 (* external-thumb-offset -1) web-post-br)
   (wall-brace thumb-top-place external-thumb-offset 0 web-post-br thumb-middle-place external-thumb-offset 0 web-post-tr)
;   (wall-brace thumb-left-place (* external-thumb-offset -1) 0 web-post-bl thumb-bottom-place (* external-thumb-offset -1) 0 web-post-tl)

   ))
