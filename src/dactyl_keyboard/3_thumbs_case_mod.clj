(ns dactyl-keyboard.3-thumbs-case-mod
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


(def three-thumb-case-walls-middle-row-mod
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
   (color-yellow
    (key-wall-brace 3 lastrow 0 -1 web-post-bl 3 lastrow 0.5 -1 web-post-br))
   (color-green
    (key-wall-brace 3 lastrow 0.5 -1 web-post-br 4 cornerrow 0.5 -1 web-post-bl))
   (for [x (range 4 ncols)]
     (key-wall-brace x cornerrow 0 -1 web-post-bl x cornerrow 0 -1 web-post-br))
   ; TODO fix extra wall
   (for [x (range 5 ncols)]
     (key-wall-brace x cornerrow 0 -1 web-post-bl (dec x) cornerrow 0 -1 web-post-br))
   ; thumb walls
   (wall-brace thumb-mr-place-mod-remove 0 -1 web-post-br thumb-r-place-mod 0 -1 thumb-post-br)
   (wall-brace thumb-mr-place-mod-remove 0 -1 web-post-br thumb-mr-place-mod-remove 0 -1 web-post-bl)
   (wall-brace thumb-br-place-mod-remove 0 -1 web-post-br thumb-br-place-mod-remove 0 -1 web-post-bl)
   (wall-brace thumb-l-place-mod 0 1 web-post-tr thumb-l-place-mod 0 1 web-post-tl)
   (wall-brace thumb-br-place-mod-remove -1 0 web-post-tl thumb-br-place-mod-remove -1 0 web-post-bl)
   (wall-brace thumb-l-place-mod -1 0 web-post-tl thumb-l-place-mod -1 0 web-post-bl)
   ; thumb corners
   (wall-brace thumb-br-place-mod-remove -1 0 web-post-bl thumb-br-place-mod-remove 0 -1 web-post-bl)
   (wall-brace thumb-l-place-mod -1 0 web-post-tl thumb-l-place-mod 0 1 web-post-tl)
   ; thumb tweeners
   (wall-brace thumb-mr-place-mod-remove 0 -1 web-post-bl thumb-br-place-mod-remove 0 -1 web-post-br)
   (wall-brace thumb-l-place-mod -1 0 web-post-bl thumb-br-place-mod-remove -1 0 web-post-tl)
   (wall-brace thumb-r-place-mod 0 -1 thumb-post-br (partial key-place 3 lastrow) 0 -1 web-post-bl)
   ; clunky bit on the top left thumb connection  (normal connectors don't work well)
   (bottom-hull
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-l-place-mod (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-l-place-mod (translate (wall-locate3 -0.3 1) web-post-tr)))
   (hull
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-l-place-mod (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-l-place-mod (translate (wall-locate3 -0.3 1) web-post-tr))
    (thumb-m-place-mod web-post-tl))
   (hull
    (left-key-place cornerrow -1 web-post)
    (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-m-place-mod web-post-tl))
   (hull
    (left-key-place cornerrow -1 web-post)
    (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
    (key-place 0 cornerrow web-post-bl)
    (thumb-m-place-mod web-post-tl))
   (hull
    (thumb-l-place-mod web-post-tr)
    (thumb-l-place-mod (translate (wall-locate1 -0.3 1) web-post-tr))
    (thumb-l-place-mod (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-l-place-mod (translate (wall-locate3 -0.3 1) web-post-tr))
    (thumb-m-place-mod web-post-tl)))
  ;
  )
(def thumb_offset 0.2)

(def three-thumb-case-matrix-border
  (let [tlc web-post-tl-c
        trc web-post-tr-c
        brc web-post-br-c
        blc web-post-bl-c]
    (union
     right-wall-inner
     ; back wall
     (for [x (range 0 ncols)]
         (key-wall-brace-inner x 0 0 border-inner-offset-vert-top web-post-tl-c x 0 0 border-inner-offset-vert-top web-post-tr-c))

     (for [x (range 1 ncols)]
         (key-wall-brace-inner x 0 0 border-inner-offset-vert-top web-post-tl-c (dec x) 0 0 border-inner-offset-vert-top web-post-tr-c))

     ; left wall
     (for [y (range 0 lastrow)]
       (color D_GRE
              (key-wall-brace-inner 0 y border-inner-offset-hor-left 0 web-post-tl-c 0 y border-inner-offset-hor-left 0 web-post-bl-c)))

     (for [y (range 1 lastrow)]
       (color D_RED
              (key-wall-brace-inner 0 y border-inner-offset-hor-left 0 web-post-tl-c 0 (dec y) border-inner-offset-hor-left 0 web-post-bl-c)))

     ; left top corner
     (color YEL (key-wall-brace-inner firstcol 0 0 border-inner-offset-vert-top tlc firstcol 0 border-inner-offset-hor-left 0 tlc))

     ; front wall
     (for [x (range 3 ncols)]
       (key-wall-brace-inner x cornerrow 0 border-inner-offset-vert-bottom web-post-bl-c x cornerrow 0 border-inner-offset-vert-bottom web-post-br-c))

     (for [x (range 4 ncols)]
       (key-wall-brace-inner x cornerrow 0 border-inner-offset-vert-bottom web-post-bl-c (dec x) cornerrow 0 border-inner-offset-vert-bottom web-post-br-c))


     ;;;;;;;;;;;;;;;
     ; thumb walls
     ;;;;;;;;;;;;;;;
     (color D_RED
            (wall-brace-inner thumb-m-place-mod 0 border-inner-offset-vert-bottom web-post-br-c thumb-m-place-mod 0 border-inner-offset-vert-bottom web-post-bl-c))

     ;diagonal between thumb and bottom left key
     (color D_BLA
            (wall-brace-inner
             (partial key-place 0 cornerrow) border-inner-offset-hor-left  0 web-post-bl-c
             thumb-m-place-mod 0 border-inner-offset-vert-top web-post-tl-c))

     (color D_RED
            (wall-brace-inner thumb-l-place-mod 0 border-inner-offset-vert-top web-post-tr-c thumb-l-place-mod 0 border-inner-offset-vert-top web-post-tl-c))
     (color D_PUR
            (wall-brace-inner thumb-l-place-mod 0 border-inner-offset-vert-bottom  web-post-br-c thumb-l-place-mod 0 border-inner-offset-vert-bottom  web-post-bl-c))
     (color D_GRE
            (wall-brace-inner thumb-l-place-mod border-inner-offset-hor-left 0 web-post-tl-c thumb-l-place-mod border-inner-offset-hor-left 0 web-post-bl-c))
     ; thumb corners
     (color-red (wall-brace-inner thumb-l-place-mod border-inner-offset-hor-left 0 web-post-bl-c thumb-l-place-mod 0 border-inner-offset-vert-bottom web-post-bl-c))
     (wall-brace-inner thumb-l-place-mod border-inner-offset-hor-left 0 web-post-tl-c thumb-l-place-mod 0 border-inner-offset-vert-top web-post-tl-c)

     ; thumb tweeners
     (color D_BLU
            (wall-brace-inner thumb-m-place-mod 0 border-inner-offset-vert-bottom web-post-bl-c thumb-l-place-mod 0 border-inner-offset-vert-bottom web-post-br-c))
     (color GRE
            (wall-brace-inner thumb-m-place-mod 0 border-inner-offset-vert-top web-post-tl-c thumb-l-place-mod 0 border-inner-offset-vert-top web-post-tr-c))
     (color D_PUR
            (wall-brace-inner thumb-m-place-mod 0 border-inner-offset-vert-bottom web-post-br-c thumb-r-place-mod 0 border-inner-offset-vert-bottom web-post-bl-c))

     ; thumb - front connection wall
     (color MAG ;(wall-brace-inner
                ; thumb-r-place-mod 0 -1 thumb-post-br-c
                ; (partial key-place 3 cornerrow) 0 -1 web-post-bl-c)
            (union
             (hull
              (thumb-r-place-mod (translate (wall-locate1 1 0) thumb-post-br-c))
              (thumb-r-place-mod (translate (wall-locate1 0 border-inner-offset-vert-bottom) thumb-post-br-c))
              ((partial key-place 3 cornerrow) web-post-bl-c)
              ((partial key-place 3 cornerrow) (translate (wall-locate1 0 border-inner-offset-vert-bottom) web-post-bl-c))))
            )

     ;right thumb
     (color-yellow
      (wall-brace-inner thumb-r-place-mod 0 border-inner-offset-vert-bottom  web-post-bl-c thumb-r-place-mod 0 border-inner-offset-vert-bottom  thumb-post-br-c))

     (color D_GRE (wall-brace-inner thumb-r-place-mod thumb-border-inner-offset-hor-right 0 web-post-tr-c thumb-r-place-mod thumb-border-inner-offset-hor-right 0 web-post-br-c))
     (color D_RED (wall-brace-inner thumb-r-place-mod 0 1 tlc thumb-r-place-mod 0 1 trc))

     ; thumb corners
     (color-green (wall-brace-inner thumb-r-place-mod thumb-border-inner-offset-hor-right 0 brc thumb-r-place-mod 0  border-inner-offset-vert-bottom brc))
     (color-blue (wall-brace-inner thumb-r-place-mod thumb-border-inner-offset-hor-right 0 trc thumb-r-place-mod 0 1 trc))

     ))
  )

(def three-thumb-case-walls-no-middle-row-mod
  (let [left_offset_x -1.0
        tlc           web-post-tl-c
        blc           web-post-bl-c]
    (union
     right-wall-out
     ;right-wall-inner
     ; back wall
     (for [x (range 0 ncols)]
       (if (not= x 1)
         (key-wall-brace-outer x 0 0 1 web-post-tl x 0 0 1 web-post-tr web-post-tl-c web-post-tr-c)))
     ;(for [x (range 0 ncols)] (if (not= x 1) (key-wall-brace-inner x 0 0 1 web-post-tl x 0 0 1 web-post-tr)))

     (for [x (range 1 ncols)]
       (if (not= x 2)
         (key-wall-brace-outer x 0 0 1 web-post-tl (dec x) 0 0 1 web-post-tr web-post-tl-c web-post-tr-c)))
     ;(for [x (range 1 ncols)] (if (not= x 2) (key-wall-brace-inner x 0 0 1 web-post-tl (dec x) 0 0 1 web-post-tr)))

     (key-wall-brace-outer 2 0 0 1 web-post-tl 2 0 -0.2 1 web-post-tr web-post-tl-c web-post-tr-c)
     ;(key-wall-brace-inner 2 0 0 1 web-post-tl 2 0 -0.2 1 web-post-tr)

     (key-wall-brace-outer 2 0 0.0 1 web-post-tl (dec 2) 0 -0.2 1 web-post-tr web-post-tl-c web-post-tr-c)
     ;(key-wall-brace-inner 2 0 0.0 1 web-post-tl (dec 2) 0 -0.2 1 web-post-tr)

     (color PIN
            (key-wall-brace-outer 1 0 0 1 web-post-tl 1 0 -0.2 1 web-post-tr web-post-tl-c web-post-tr-c))
     ;(color PIN (key-wall-brace-inner 1 0 0 1 web-post-tl 1 0 -0.2 1 web-post-tr))

     ; left wall
     (for [y (range 0 lastrow)]
       (union
        (wall-brace-bottom (partial left-key-place y 1) -1 0 web-post (partial left-key-place y -1) -1 0 web-post)
        (color PUR
               (hull
                (key-wall-brace-inner-edge 0 y -1.5 0 web-post-tl-c 0 y -1.5 0 web-post-bl-c)
                (key-wall-brace-inner-edge 0 y -1.0 0 web-post-tl-c 0 y -1.0 0 web-post-bl-c)
                (wall3-brace-edge (partial left-key-place y -1) -1 0 web-post (partial left-key-place y 1) -1 0 web-post)))))

     (for [y (range 1 lastrow)]
       (union
        (wall-brace-bottom (partial left-key-place (dec y) -1) -1 0 web-post (partial left-key-place y 1) -1 0 web-post)
        (color-yellow
         (hull
          (key-wall-brace-inner-edge 0 (dec y) -1.5 0 web-post-bl-c 0 y -1.5 0 web-post-tl-c)
          (key-wall-brace-inner-edge 0 (dec y) -1.0 0 web-post-bl-c 0 y -1.0 0 web-post-tl-c)
          (wall3-brace-edge (partial left-key-place (dec y) -1) -1 0 web-post (partial left-key-place y 1) -1 0 web-post)))))


     ;top left corner
     (color-orange
      (hull
       ;left top point
       (wall3-brace-edge-point (partial left-key-place 0 1) -1 0 web-post)

       ;top left point
       ((partial key-place firstcol firstrow)
         (translate (wall-locate3 0 1) web-post-tl))
       ((partial key-place firstcol firstrow) (translate (wall-locate1 0 1) tlc))

       ((partial key-place firstcol firstrow) (translate (wall-locate1 -1 0) tlc))
       ((partial key-place firstcol firstrow) (translate (wall-locate1 -1.5 0) tlc))))
     (bottom-hull
      (wall3-brace-edge-point (partial left-key-place 0 1) -1 0 web-post)

      ;top left point
      ((partial key-place firstcol firstrow)
        (translate (wall-locate3 0 1) web-post-tl)))

     ; front wall
     (for [x (range 3 ncols)]
       (key-wall-brace-outer x cornerrow 0 -1 web-post-bl x cornerrow 0 -1 web-post-br web-post-bl-c web-post-br-c))
     ;(for [x (range 3 ncols)] (key-wall-brace-inner x cornerrow 0 -1 web-post-bl x cornerrow 0 -1 web-post-br))

     (for [x (range 4 ncols)]
       (key-wall-brace-outer x cornerrow 0 -1 web-post-bl (dec x) cornerrow 0 -1 web-post-br web-post-bl-c web-post-br-c))
     ;(for [x (range 4 ncols)] (key-wall-brace-inner x cornerrow 0 -1 web-post-bl (dec x) cornerrow 0 -1 web-post-br))

     ;;;;;;;;;;;;;;;
     ; thumb walls
     ;;;;;;;;;;;;;;;
     (color-green
      (wall-brace-outer thumb-r-place-mod 0 -1 web-post-bl thumb-r-place-mod 0 -1 thumb-post-br web-post-bl-c thumb-post-br-c))


     (wall-brace-outer thumb-m-place-mod 0 -1 web-post-br thumb-m-place-mod 0 -1 web-post-bl web-post-br-c web-post-bl-c)
     (wall-brace-outer thumb-l-place-mod 0 -1 web-post-br thumb-l-place-mod 0 -1 web-post-bl web-post-br-c web-post-bl-c)
     (wall-brace-outer thumb-l-place-mod 0 1 web-post-tr thumb-l-place-mod 0 1 web-post-tl web-post-tr-c web-post-tl-c)
     (wall-brace-outer thumb-l-place-mod -1 0 web-post-tl thumb-l-place-mod -1 0 web-post-bl web-post-tl-c web-post-bl-c)

     ; thumb corners
     (wall-brace-outer thumb-l-place-mod -1 0 web-post-bl thumb-l-place-mod 0 -1 web-post-bl web-post-bl-c web-post-bl-c)
     (wall-brace-outer thumb-l-place-mod -1 0 web-post-tl thumb-l-place-mod 0 1 web-post-tl web-post-tl-c web-post-tl-c)
     ; thumb tweeners
     (color-orange
      (wall-brace-outer thumb-m-place-mod 0 -1 web-post-bl thumb-l-place-mod 0 -1 web-post-br web-post-bl-c web-post-br-c))
     (color-orange
      (wall-brace-outer thumb-m-place-mod 0 -1 web-post-br thumb-r-place-mod 0 -1 web-post-bl web-post-br-c web-post-bl-c))
     ; thumb - front connection wall

     (color D_RED
            (wall-brace-outer thumb-r-place-mod 0 -1 thumb-post-br (partial key-place 3 cornerrow) 0 -1 web-post-bl thumb-post-br-c web-post-bl-c))

     ; clunky bit on the top left thumb connection  (normal connectors don't work well)
     (color D_BLU (bottom-hull
      (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
      (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
      (thumb-l-place-mod (translate (wall-locate2 -0.3 1) web-post-tr))
      (thumb-l-place-mod (translate (wall-locate3 -0.3 1) web-post-tr))))

     (color GRE
            (hull
             (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
             (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
             (thumb-l-place-mod (translate (wall-locate2 -0.3 1) web-post-tr))
             (thumb-l-place-mod (translate (wall-locate3 -0.3 1) web-post-tr))
             ;(thumb-m-place-mod web-post-tl)
             (wall1-brace-edge-point thumb-m-place-mod 0 1 web-post-tl-c)))
     (color PIN
            (hull
             (left-key-place cornerrow -1 web-post)
             (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
             (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
             (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
             ;(thumb-m-place-mod web-post-tl)
             (wall1-brace-edge-point thumb-m-place-mod 0 1 web-post-tl-c)))
     (color RED
            (difference
             (hull
              (left-key-place cornerrow -1 web-post)
              (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))

              (key-wall-brace-inner-point 0 cornerrow -1.5 0 blc)

              ;                 (wall3-brace-edge-point (partial web-post-tl-c cornerrow -1) 0 1 web-post-bl-c)
              ;(thumb-m-place-mod web-post-tl)
              (wall1-brace-edge-point thumb-m-place-mod 0 1 web-post-tl-c))
             (wall-brace-inner
              (partial key-place 0 cornerrow) -1 0 web-post-bl-c
              thumb-m-place-mod 0 1 web-post-tl-c)
             ; end diff
             ))

     ;(color GRE
     ;            (wall-brace-inner thumb-m-place-mod 0 1 web-post-tl-c thumb-l-place-mod 0 1 web-post-tr-c))
     (color CYA
            (hull
             ;(thumb-l-place-mod web-post-tr)
             ;(wall-brace-no-bottom thumb-l-place-mod 0 1 web-post-tr-c thumb-m-place-mod 0 1 web-post-tl-c )
             (wall1-brace-edge-point thumb-l-place-mod 0 1 web-post-tr-c)
             (thumb-l-place-mod (translate (wall-locate1 0 1) web-post-tr))
             (thumb-l-place-mod (translate (wall-locate2 -0.3 1) web-post-tr))
             (thumb-l-place-mod (translate (wall-locate3 -0.3 1) web-post-tr))
             ;(thumb-m-place-mod web-post-tl)
             (wall1-brace-edge-point thumb-m-place-mod 0 1 web-post-tl-c))))
    ;
    ))

(def three-thumb-case-walls-mod
  (if extra-middle-row
    three-thumb-case-walls-middle-row-mod
    three-thumb-case-walls-no-middle-row-mod))
