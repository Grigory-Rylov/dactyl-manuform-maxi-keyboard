(ns dactyl-keyboard.5-thumbs-connectors-mod
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

(def five-thumbs-connectors-extra-row-mod
  (union
   (triangle-hulls    ; top two
    (thumb-m-place-mod web-post-tr)
    (thumb-m-place-mod web-post-br)
    (thumb-r-place-mod thumb-post-tl)
    (thumb-r-place-mod thumb-post-bl))
   (triangle-hulls    ; bottom two
    (thumb-l2-place-mod web-post-tr)
    (thumb-l2-place-mod web-post-br)
    (thumb-m2-place-mod web-post-tl)
    (thumb-m2-place-mod web-post-bl))
   (triangle-hulls
    (thumb-m2-place-mod web-post-tr)
    (thumb-m2-place-mod web-post-br)
    (thumb-r-place-mod thumb-post-br))
   (triangle-hulls    ; between top row and bottom row
    (thumb-l2-place-mod web-post-tl)
    (thumb-l-place-mod web-post-bl)
    (thumb-l2-place-mod web-post-tr)
    (thumb-l-place-mod web-post-br)
    (thumb-m2-place-mod web-post-tl)
    (thumb-m-place-mod web-post-bl)
    (thumb-m2-place-mod web-post-tr)
    (thumb-m-place-mod web-post-br)
    (thumb-r-place-mod web-post-bl)
    (thumb-m2-place-mod web-post-tr)
    (thumb-r-place-mod web-post-br))
   (triangle-hulls    ; top two to the middle two, starting on the left
    (thumb-m-place-mod web-post-tl)
    (thumb-l-place-mod web-post-tr)
    (thumb-m-place-mod web-post-bl)
    (thumb-l-place-mod web-post-br)
    (thumb-m2-place-mod web-post-tr)
    (thumb-m-place-mod web-post-bl)
    (thumb-m-place-mod web-post-br)
    (thumb-m2-place-mod web-post-tr))
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

;; thumb--tl-place - > thumb-m-place-mod
;; thumb--tr-place - > thumb-r-place-mod
;thumb--br-place -> thumb-l2-place-mod
;thumb--bl-place -> thumb-l-place-mod
;thumb-mr-place-mod ->  thumb-m2-place-mod
(def five-thumbs-connectors-no-extra-row-mod
  (let [tr  web-post-tr-c
        tl  web-post-tl-c
        br  web-post-br-c
        bl  web-post-bl-c

        tlc web-post-tl-c
        trc web-post-tr-c
        brc web-post-br-c
        blc web-post-bl-c]
    (union
     ; inner thumb connections
     (color PIN
            (triangle-hulls    ; top two
             (thumb-m-place-mod tr)
             (thumb-m-place-mod br)
             (thumb-r-place-mod thumb-post-tl-c)
             (thumb-r-place-mod thumb-post-bl-c))
            (triangle-hulls    ; bottom two
             (thumb-l2-place-mod tr)
             (thumb-l2-place-mod br)
             (thumb-m2-place-mod tl)
             (thumb-m2-place-mod bl))
            (triangle-hulls
             (thumb-m2-place-mod tr)
             (thumb-m2-place-mod br)
             (thumb-r-place-mod thumb-post-br-c))
            (triangle-hulls    ; between top row and bottom row
             (thumb-l2-place-mod tl)
             (thumb-l-place-mod bl)
             (thumb-l2-place-mod tr)
             (thumb-l-place-mod br)
             (thumb-m2-place-mod tl)
             (thumb-m-place-mod bl)
             (thumb-m2-place-mod tr)
             (thumb-m-place-mod br)
             (thumb-r-place-mod bl)
             (thumb-m2-place-mod tr)
             (thumb-r-place-mod br))
            (triangle-hulls    ; top two to the middle two, starting on the left
             (thumb-m-place-mod tl)
             (thumb-l-place-mod tr)
             (thumb-m-place-mod bl)
             (thumb-l-place-mod br)
             (thumb-m2-place-mod tr)
             (thumb-m-place-mod bl)
             (thumb-m-place-mod br)
             (thumb-m2-place-mod tr)))
     (color PIN
            (triangle-hulls    ; top two to the main keyboard, starting on the left
             (thumb-m-place-mod web-post-tl)
             (key-place 0 cornerrow web-post-bl-c)
             (thumb-m-place-mod web-post-tr)
             (key-place 0 cornerrow web-post-br)))

     (color YEL
            (hull
             (wall-brace-inner-edge thumb-r-place-mod 1 0 web-post-br-c thumb-r-place-mod 1 0 web-post-tr-c)
             (key-place 3 cornerrow web-post-bl)))

     (color MAG
            (hull
             (thumb-r-place-mod (translate (wall-locate1 1 0) thumb-post-tr-c))
             (thumb-r-place-mod (translate (wall-locate1 0 1) thumb-post-tr))

             ;     (key-place 2 lastrow web-post-tl)
             (key-place 3 cornerrow web-post-bl)
             (key-place 1 cornerrow web-post-br)
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
       ;(key-place 1 lastrow web-post-tl)
       ;(key-wall-brace-inner-point 1 cornerrow 0 -1 web-post-bl)
       (key-wall-brace-inner-point 1 cornerrow 0.7 -1 web-post-bl)
       (key-place 0 cornerrow web-post-br)
       (thumb-m-place-mod thumb-post-tr)))

     (key-wall-brace-inner 1 cornerrow 0 -1 web-post-bl-c 1 cornerrow 0 -1 web-post-br-c)

     (color-yellow
      (hull
       (key-place 0 cornerrow web-post-br)

       (key-wall-brace-inner-point 1 cornerrow 0.7 -1 web-post-bl)

       (key-wall-brace-inner-point 1 cornerrow 0 -1 web-post-bl)
       (key-wall-brace-inner-point 1 cornerrow 0 0 web-post-bl)
       (key-wall-brace-inner-point 0 cornerrow 0 0 web-post-br)))

     (key-wall-brace-inner 2 cornerrow 0 -1 web-post-bl-c 1 cornerrow 0 -1 web-post-br-c)

     (color CYA
            (hull
             (thumb-r-place-mod (translate (wall-locate1 0 1) thumb-post-tr))
             (thumb-r-place-mod (translate (wall-locate1 0 1) thumb-post-tl))

             (key-wall-brace-inner-point 1 cornerrow 0.7 -1 web-post-bl)

             (key-wall-brace-inner-point 1 cornerrow 0 0 web-post-br)))

     (color-red
      (triangle-hulls
       (key-place 1 cornerrow web-post-br)
       (key-place 2 lastrow web-post-tl)
       (key-place 2 cornerrow web-post-bl)
       (key-place 2 lastrow web-post-tr)
       (key-place 2 cornerrow web-post-br)
       (key-place 3 cornerrow web-post-bl))))))

(def five-thumbs-connectors-mod
  (if extra-middle-row
    five-thumbs-connectors-extra-row-mod
    five-thumbs-connectors-no-extra-row-mod))
