(ns dactyl-keyboard.3-thumbs-connectors-mod
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.connectors-common :refer :all]
            [dactyl-keyboard.case-common :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.config :refer :all]))


(def three-thumbs-connectors-extra-row-mod
  (union
   (triangle-hulls    ; top two
    (thumb-m-place-mod web-post-tr)
    (thumb-m-place-mod web-post-br)
    (thumb-r-place-mod thumb-post-tl)
    (thumb-r-place-mod thumb-post-bl))
   (triangle-hulls    ; bottom two
    (thumb-br-place-mod-remove web-post-tr)
    (thumb-br-place-mod-remove web-post-br)
    (thumb-mr-place-mod-remove web-post-tl)
    (thumb-mr-place-mod-remove web-post-bl))
   (triangle-hulls
    (thumb-mr-place-mod-remove web-post-tr)
    (thumb-mr-place-mod-remove web-post-br)
    (thumb-r-place-mod thumb-post-br))
   (triangle-hulls    ; between top row and bottom row
    (thumb-br-place-mod-remove web-post-tl)
    (thumb-l-place-mod web-post-bl)
    (thumb-br-place-mod-remove web-post-tr)
    (thumb-l-place-mod web-post-br)
    (thumb-mr-place-mod-remove web-post-tl)
    (thumb-m-place-mod web-post-bl)
    (thumb-mr-place-mod-remove web-post-tr)
    (thumb-m-place-mod web-post-br)
    (thumb-r-place-mod web-post-bl)
    (thumb-mr-place-mod-remove web-post-tr)
    (thumb-r-place-mod web-post-br))
   (triangle-hulls    ; top two to the middle two, starting on the left
    (thumb-m-place-mod web-post-tl)
    (thumb-l-place-mod web-post-tr)
    (thumb-m-place-mod web-post-bl)
    (thumb-l-place-mod web-post-br)
    (thumb-mr-place-mod-remove web-post-tr)
    (thumb-m-place-mod web-post-bl)
    (thumb-m-place-mod web-post-br)
    (thumb-mr-place-mod-remove web-post-tr))
   (triangle-hulls    ; top two to the main keyboard, starting on the left
    (thumb-m-place-mod web-post-tl)
    (key-place 0 cornerrow web-post-bl)
    (thumb-m-place-mod web-post-tr)
    (key-place 0 cornerrow web-post-br)
    (thumb-r-place-mod thumb-post-tl)
    (key-place 1 cornerrow web-post-bl)
    (thumb-r-place-mod thumb-post-tr)
    (key-place 1 cornerrow web-post-br)
    (key-place 2 lastrow web-post-bl)
    (thumb-r-place-mod thumb-post-tr)
    (key-place 2 lastrow web-post-bl)
    (thumb-r-place-mod thumb-post-br)
    (key-place 2 lastrow web-post-br)
    (key-place 3 lastrow web-post-bl)
    (key-place 2 lastrow web-post-tr)
    (key-place 3 lastrow web-post-tl)
    (key-place 3 cornerrow web-post-bl)
    (key-place 3 lastrow web-post-tr)
    (key-place 3 cornerrow web-post-br)
    (key-place 4 cornerrow web-post-bl))
   (triangle-hulls    ; top two to the main keyboard, starting on the left
    (key-place 1 cornerrow web-post-br)
    (key-place 2 lastrow web-post-tl)
    (key-place 2 lastrow web-post-bl))
   (triangle-hulls
    (key-place 1 cornerrow web-post-br)
    (key-place 2 lastrow web-post-tl)
    (key-place 2 cornerrow web-post-bl)
    (key-place 2 lastrow web-post-tr)
    (key-place 2 cornerrow web-post-br)
    (key-place 3 cornerrow web-post-bl))
   (triangle-hulls
    (key-place 3 lastrow web-post-tr)
    (key-place 3 lastrow web-post-br)
    (key-place 3 lastrow web-post-tr)
    (key-place 4 cornerrow web-post-bl))))

(def thumb_offset 0.2)
(def three-thumbs-connectors-no-extra-row-mod
  (union
   (color PUR
          (triangle-hulls    ; top two
           (thumb-m-place-mod web-post-tr)
           (thumb-m-place-mod web-post-br)
           (thumb-r-place-mod thumb-post-tl)
           (thumb-r-place-mod thumb-post-bl)))


   (triangle-hulls    ; top two to the middle two, starting on the left
    (thumb-m-place-mod web-post-tl)
    (thumb-l-place-mod web-post-tr)
    (thumb-m-place-mod web-post-bl)
    (thumb-l-place-mod web-post-br))

   ;(color PIN
   ;       (hull
   ;        (thumb-r-place-mod thumb-post-tr)
   ;        (thumb-r-place-mod thumb-post-tl)
   ;        (thumb-r-place-mod (translate (wall-locate3 0 thumb_offset) thumb-post-tr))
   ;        ))

   (color GRE
          (triangle-hulls    ; top two to the main keyboard, starting on the left
           (thumb-m-place-mod web-post-tl)
           (key-place 0 cornerrow web-post-bl)
           (thumb-m-place-mod web-post-tr)
           (key-place 0 cornerrow web-post-br)
           ;(thumb-r-place-mod thumb-post-tl)
           ;(key-place 1 cornerrow web-post-bl)

           ;      (thumb-r-place-mod thumb-post-tr)
           ;(thumb-r-place-mod (translate (wall-locate3 0 thumb_offset) thumb-post-tr))

           ;      (key-place 1 cornerrow web-post-br)


           ;(key-place 2 lastrow web-post-bl)

           ;(thumb-r-place-mod thumb-post-tr)
           ;(thumb-r-place-mod (translate (wall-locate3 0 thumb_offset ) thumb-post-tr))

           ;(key-place 2 lastrow web-post-bl)

           ;(thumb-r-place-mod thumb-post-br)
           ;(thumb-r-place-mod (translate (wall-locate3 0 thumb_offset ) thumb-post-br))
           ;(wall-brace-no-bottom-place thumb-r-place-mod 0 thumb_offset web-post-tl)

           ;       (key-place 3 cornerrow web-post-bl)

           ;(key-place 3 lastrow web-post-bl)
           ;(key-place 2 lastrow web-post-tr)
           ;(key-place 3 lastrow web-post-tl)
           ;(key-place 3 cornerrow web-post-bl)
           ;(key-place 3 lastrow web-post-tr)
           ;(key-place 3 cornerrow web-post-br)
           ;(key-place 4 cornerrow web-post-bl)
           ))

   (color CYA
          (hull
           (key-place 1 lastrow web-post-tl)
           (key-place 1 lastrow web-post-tr)

           (thumb-r-place-mod thumb-post-tr)
           (thumb-r-place-mod thumb-post-tl)))

   (color YEL
          (hull
           (wall-brace-inner-edge thumb-r-place-mod 1 0 web-post-br-c thumb-r-place-mod 1 0 web-post-tr-c)
           (key-place 3 cornerrow web-post-bl)))

   ; triangle between thumb and 2 col
   (color-blue
    (hull
     (thumb-r-place-mod (translate (wall-locate1 1 0) thumb-post-tr))
     (thumb-r-place-mod (translate (wall-locate1 0 1) thumb-post-tr))

     (key-place 3 cornerrow web-post-bl)
     (key-place 1 lastrow web-post-tr)
     ;
     ))
   (color-yellow
    (hull
     (key-place 1 cornerrow web-post-br)
     (key-place 3 cornerrow web-post-bl)
     (key-place 1 lastrow web-post-tr)
     ;
     ))

   (color-green
    (hull
     (key-place 3 cornerrow web-post-bl)
     (key-place 2 lastrow web-post-tl)
     (key-place 2 lastrow web-post-tr)
     (key-place 1 cornerrow web-post-br)
     ;
     ))

   (color-orange
    (hull
     (thumb-r-place-mod thumb-post-tl)
     (key-place 1 lastrow web-post-tl)
     (key-place 0 cornerrow web-post-br)
     (thumb-m-place-mod thumb-post-tr)))

   (color RED
          (hull
           (key-place 1 lastrow web-post-tl)
           (key-place 1 cornerrow web-post-bl)

           (key-place 0 cornerrow web-post-br)))

   (color ORA
     (hull
      (key-place 1 lastrow web-post-tl)
      (key-place 1 cornerrow web-post-bl)
      (key-place 1 lastrow web-post-tr)
      (key-place 1 cornerrow web-post-br)))

   (color-red
    (triangle-hulls
     (key-place 1 cornerrow web-post-br)
     (key-place 2 lastrow web-post-tl)
     (key-place 2 cornerrow web-post-bl)
     (key-place 2 lastrow web-post-tr)
     (key-place 2 cornerrow web-post-br)
     (key-place 3 cornerrow web-post-bl)))))

(def three-thumbs-connectors-mod
  (if extra-middle-row
    three-thumbs-connectors-extra-row-mod
    three-thumbs-connectors-no-extra-row-mod))
