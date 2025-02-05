(ns dactyl-keyboard.5-thumbs-case-mod
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

(def five-thumb-case-matrix-border-right-mod
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
            (wall-brace-inner thumb-m2-place-mod 0 border-inner-offset-vert-bottom web-post-br-c thumb-m2-place-mod 0 border-inner-offset-vert-bottom web-post-bl-c))

     ;diagonal between thumb and bottom left key
     (color D_BLA
            (wall-brace-inner
             (partial key-place 0 cornerrow) border-inner-offset-hor-left  0 web-post-bl-c
             thumb-m-place-mod 0 border-inner-offset-vert-top web-post-tl-c))

     (color D_RED
            (wall-brace-inner thumb-l-place-mod 0 border-inner-offset-vert-top web-post-tr-c thumb-l-place-mod 0 border-inner-offset-vert-top web-post-tl-c))
     (color D_PUR
            (wall-brace-inner thumb-l2-place-mod 0 border-inner-offset-vert-bottom  web-post-br-c thumb-l2-place-mod 0 border-inner-offset-vert-bottom  web-post-bl-c))

     (color D_BLU
            (wall-brace-inner thumb-l-place-mod border-inner-offset-hor-left 0 web-post-tl-c thumb-l-place-mod border-inner-offset-hor-left 0 web-post-bl-c))
     (color D_BLU
            (wall-brace-inner thumb-l2-place-mod border-inner-offset-hor-left 0 web-post-tl-c thumb-l2-place-mod border-inner-offset-hor-left 0 web-post-bl-c))

     (color D_GRE
            (wall-brace-inner
             thumb-l-place-mod border-inner-offset-hor-left 0 web-post-bl-c
             thumb-l2-place-mod border-inner-offset-hor-left 0 web-post-tl-c))

     ; thumb corners
     (color-yellow (wall-brace-inner thumb-l2-place-mod border-inner-offset-hor-left 0 web-post-bl-c thumb-l2-place-mod 0 border-inner-offset-vert-bottom web-post-bl-c))
     (wall-brace-inner thumb-l-place-mod border-inner-offset-hor-left 0 web-post-tl-c thumb-l-place-mod 0 border-inner-offset-vert-top web-post-tl-c)

     ; thumb tweeners
     (color D_BLU
            (wall-brace-inner thumb-m2-place-mod 0 border-inner-offset-vert-bottom web-post-bl-c thumb-l2-place-mod 0 border-inner-offset-vert-bottom web-post-br-c))
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

     (color D_GRE (wall-brace-inner thumb-r-place-mod thumb-border-inner-offset-hor-right 0 web-post-tr-c thumb-r-place-mod thumb-border-inner-offset-hor-right 0 web-post-br-c))
     (color D_RED (wall-brace-inner thumb-r-place-mod 0 1 tlc thumb-r-place-mod 0 1 trc))

     ; thumb corners
    (color D_GRE (wall-brace-inner
                  thumb-r-place-mod thumb-border-inner-offset-hor-right 0 brc
                  thumb-m2-place-mod 0  border-inner-offset-vert-bottom brc
                  ))
     (color-blue (wall-brace-inner thumb-r-place-mod thumb-border-inner-offset-hor-right 0 trc thumb-r-place-mod 0 1 trc))

     ))
  )

(def fifth-thumb-case-walls-middle-row-mod
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
    (thumb-tl-place web-post-tl)))
  ;
  )

(def fifth-thumb-case-walls-no-middle-row-mod
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
     (color-blue
      (union
       (wall-brace (partial left-key-place y 1) -1 0 web-post (partial left-key-place y -1) -1 0 web-post)
       (hull (key-place 0 y web-post-tl)
             (key-place 0 y web-post-bl)
             (left-key-place y 1 web-post)
             (left-key-place y -1 web-post)))))
   (for [y (range 1 lastrow)]
     (color-yellow
      (union
       (wall-brace (partial left-key-place (dec y) -1) -1 0 web-post (partial left-key-place y 1) -1 0 web-post)
       (hull (key-place 0 y web-post-tl)
             (key-place 0 (dec y) web-post-bl)
             (left-key-place y 1 web-post)
             (left-key-place (dec y) -1 web-post)))))
   ; left top corner
   (color-green
    (wall-brace
     (partial key-place 0 0) ; place 1
     0 1 ; dx1 dy1
     web-post-tl ; post1
     (partial left-key-place 0 1) ; place2
     0 0.5 ; dx2 dy2
     web-post ; post2
     ))
   (color-red
    (wall-brace (partial left-key-place 0 1) ; place1
                0 0.5 ; dx1 dy1
                web-post ; post1
                (partial left-key-place 0 1) ; place2
                -1 0 ; dx2 dy2
                web-post ;post2
                ))

   ; front wall
   (for [x (range 3 ncols)]
     (key-wall-brace x cornerrow 0 -1 web-post-bl x cornerrow 0 -1 web-post-br))
   (for [x (range 4 ncols)]
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
   ; thumb - front connection wall

   (wall-brace thumb-tr-place 0 -1 thumb-post-br (partial key-place 3 cornerrow) 0 -1 web-post-bl)

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
    (thumb-tl-place web-post-tl)))
  ;
  )

(def fifth-thumb-case-walls-mod
  (if extra-middle-row
    fifth-thumb-case-walls-middle-row-mod
    fifth-thumb-case-walls-no-middle-row-mod))
