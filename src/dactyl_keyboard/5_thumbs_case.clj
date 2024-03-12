(ns dactyl-keyboard.5-thumbs-case
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


(def fifth-thumb-case-walls
  (union
   right-wall
   ; back wall
   (for [x (range 0 ncols)]
     (if (not= x 1) (key-wall-brace x 0 0 1 web-post-tl x 0 0 1 web-post-tr)))
   (for [x (range 1 ncols)]
     (if (not= x 2) (key-wall-brace x 0 0 1 web-post-tl (dec x) 0 0 1 web-post-tr)))
   (key-wall-brace 2 0 0 1 web-post-tl 2 0 -0.2 1 web-post-tr)
   (key-wall-brace 2 0 0.0 1 web-post-tl (dec 2) 0 -0.2 1 web-post-tr)
   (key-wall-brace 1 0 0 1 web-post-tl 1 0 -0.2 1 web-post-tr)

   ; left wall
   (for [y (range 0 lastrow)]
     (union
       (wall-brace (partial left-key-place y 1) -1 0 web-post (partial left-key-place y -1) -1 0 web-post)
       (hull (key-place 0 y web-post-tl)
             (key-place 0 y web-post-bl)
             (left-key-place y 1 web-post)
             (left-key-place y -1 web-post))))
   (for [y (range 1 lastrow)]
     (union
       (wall-brace (partial left-key-place (dec y) -1) -1 0 web-post (partial left-key-place y 1) -1 0 web-post)
       (hull (key-place 0 y web-post-tl)
             (key-place 0 (dec y) web-post-bl)
             (left-key-place y 1 web-post)
             (left-key-place (dec y) -1 web-post))))
   (wall-brace (partial key-place 0 0) 0 1 web-post-tl (partial left-key-place 0 1) 0 1 web-post)
   (wall-brace (partial left-key-place 0 1) 0 1 web-post (partial left-key-place 0 1) -1 0 web-post)
   ; front wall
   (key-wall-brace 3 lastrow 0 -1 web-post-bl 3 lastrow 0.5 -1 web-post-br)
   (key-wall-brace 3 lastrow 0.5 -1 web-post-br 4 cornerrow 0.5 -1 web-post-bl)
   (for [x (range 4 ncols)]
     (key-wall-brace x cornerrow 0 -1 web-post-bl x cornerrow 0 -1 web-post-br))
   ; TODO fix extra wall
   (for [x (range 5 ncols)]
     (key-wall-brace x cornerrow 0 -1 web-post-bl (dec x) cornerrow 0 -1 web-post-br))
   ; thumb walls
   (wall-brace thumb-mr-place 0 -1 web-post-br thumb-tr-place 0 -1 thumb-post-br)
   (wall-brace thumb-mr-place 0 -1 web-post-br thumb-mr-place 0 -1 web-post-bl)
   (wall-brace thumb-br-place 0 -1 web-post-br thumb-br-place 0 -1 web-post-bl)
   (wall-brace thumb-bl-place 0 1 web-post-tr thumb-bl-place 0 1 web-post-tl)
   (wall-brace thumb-br-place -1 0 web-post-tl thumb-br-place -1 0 web-post-bl)
   (wall-brace thumb-bl-place -1 0 web-post-tl thumb-bl-place -1 0 web-post-bl)
   ; thumb corners
   (wall-brace thumb-br-place -1 0 web-post-bl thumb-br-place 0 -1 web-post-bl)
   (wall-brace thumb-bl-place -1 0 web-post-tl thumb-bl-place 0 1 web-post-tl)
   ; thumb tweeners
   (wall-brace thumb-mr-place 0 -1 web-post-bl thumb-br-place 0 -1 web-post-br)
   (wall-brace thumb-bl-place -1 0 web-post-bl thumb-br-place -1 0 web-post-tl)
   (wall-brace thumb-tr-place 0 -1 thumb-post-br (partial key-place 3 lastrow) 0 -1 web-post-bl)
   ; clunky bit on the top left thumb connection  (normal connectors don't work well)
   (bottom-hull
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-bl-place (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-bl-place (translate (wall-locate3 -0.3 1) web-post-tr)))
   (hull
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-bl-place (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-bl-place (translate (wall-locate3 -0.3 1) web-post-tr))
    (thumb-tl-place web-post-tl))
   (hull
    (left-key-place cornerrow -1 web-post)
    (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-tl-place web-post-tl))
   (hull
    (left-key-place cornerrow -1 web-post)
    (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
    (key-place 0 cornerrow web-post-bl)
    (thumb-tl-place web-post-tl))
   (hull
    (thumb-bl-place web-post-tr)
    (thumb-bl-place (translate (wall-locate1 -0.3 1) web-post-tr))
    (thumb-bl-place (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-bl-place (translate (wall-locate3 -0.3 1) web-post-tr))
    (thumb-tl-place web-post-tl))))
