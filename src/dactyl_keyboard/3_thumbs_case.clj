(ns dactyl-keyboard.3-thumbs-case
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

(def mid-wall-top-offset -1.5)

(def three-thumbs-case-walls
  (union
   right-wall
   ; back wall
   (for [x (range 0 ncols)]
     (if (and (not= x 1) (not= x 2))
       (key-wall-brace x 0 0 1 web-post-tl x 0 0 1 web-post-tr)))

   (for [x (range 1 ncols)]
     (if (not= x 2) (key-wall-brace x 0 0 1 web-post-tl (dec x) 0 0 1 web-post-tr)))

   ;back 1 , 2 cols
   (color-yellow
    (key-wall-brace 2 0 ; x1 y1
                    -0.3 1 ; dx1 dy
                    web-post-tl ; post1
                    2 0 ; x2 y2
                    0 1 ; dx2 dy2
                    web-post-tr ; post2
                    ))

   (color-blue (key-wall-brace 2 0 -0.3 1 web-post-tl (dec 2) 0 -0.5 1 web-post-tr))

   (color-green
    (key-wall-brace 1 0 ; x1 y1
                    0 1 ; dx1 dy1
                    web-post-tl ; post1
                    1 0 ; x2 y2
                    -0.5 1 ; dx2 dy2
                    web-post-tr))
   ;mid
   ;front
   (if mono-mode
     (bottom-hull
      (translate (wall-locate-mid -3 -1.3 -0.85)
                 (left-key-place 0 1 (binding [*fn* wall-fn] (sphere 2))))
      (translate (wall-locate-mid -24 -1.3 -0.85)
                 (left-key-place 0 1 (binding [*fn* wall-fn] (sphere 2))))))

   ;back
   (if mono-mode
     (bottom-hull
      (translate (wall-locate-mid -3 -1.3 -0.9)
                 (left-key-place 2 -1 (binding [*fn* wall-fn] (sphere 2))))
      (translate (wall-locate-mid -24 -1.0 -0.9)
                 (left-key-place 2 -1 (binding [*fn* wall-fn] (sphere 2))))))

   ; left key wall
   (for [y (range 0 nrows)]
     (union

      (if mono-mode
        (wall-brace-top (partial left-key-place y 1)
                        -1 0
                        web-post
                        (partial left-key-place y -1)
                        -1 0
                        web-post)
        ; else
        (wall-brace (partial left-key-place y 1) -1 0 web-post (partial left-key-place y -1) -1 0 web-post))

      (hull (key-place 0 y web-post-tl)
            (key-place 0 y web-post-bl)
            (left-key-place y 1 web-post)
            (left-key-place y -1 web-post))))

   ;; left keyplace mid
   (for [y (range 0 (dec nrows))]
     (union
      (if mono-mode
        (wall-brace-top (partial left-key-place y -1) -1 0 web-post (partial left-key-place (inc y) 1) -1 0 web-post)
        ; else
        (wall-brace (partial left-key-place y -1) -1 0 web-post (partial left-key-place (inc y) 1) -1 0 web-post))
      (hull (key-place 0 (inc y) web-post-tl)
            (key-place 0 y web-post-bl)
            (left-key-place (inc y) 1 web-post)
            (left-key-place y -1 web-post))))
   ; left back corner
   (color-red
    (wall-brace (partial key-place 0 0) 0 1 web-post-tl (partial left-key-place 0 1) 0 0.5 web-post))
   (color-blue
    (wall-brace (partial left-key-place 0 1) 0 0.5 web-post (partial left-key-place 0 1) -1 0 web-post))

   ; front wall

   ;??? horizontal panels
   (for [x (range 3 (- ncols 0))]
     (key-wall-brace x cornerrow 0 -1 web-post-bl x cornerrow 0 -1 web-post-br))

   ;front connections between colums
   (for [x (range 4 ncols)]
     (color-blue
      (key-wall-brace x cornerrow 0 -1 web-post-bl (dec x) cornerrow 0 -1 web-post-br)))

   ; last and last -1 columns
   ;   (color-yellow
   ;    (key-wall-brace 4 cornerrow ;x1 y1
   ;                    -0.5 -1 ;dx1 dy1
   ;                    web-post-bl ; post1
   ;                    4 cornerrow ;x2 y2
   ;                    0 -1 ; dx1 dy1
   ;                    web-post-br ; post2
   ;                    ))

   ;;front
   ; connections between colums
   ;(color-blue (key-wall-brace 4 cornerrow -0.5 -1 web-post-bl 3 cornerrow -0.5 -1 web-post-br))

   ;(color-gray (key-wall-brace 3 cornerrow 0 -1 web-post-bl 3 cornerrow -0.5 -1 web-post-br))


   ;wall between key 1 and thumb
   ; TODO remove bottom wall
   (color PIN
    (key-wall-brace-top
     2 ; x1
     cornerrow ; y1
     0 ; dx1
     -0.5 ; dy1
     web-post-bl ; post1
     2 ; x2
     cornerrow ; y2
     0 ; dx2
     -1 ; dy2
     web-post-br))


   ;wall near thumb cluster
   ;(color-blue(wall-brace
   ; (partial key-place 1 cornerrow) 1 0 web-post-br
   ; (partial key-place 2 cornerrow) 1 -1 web-post-bl))

   ; thumb walls
   (color MAG
          (wall-brace
           thumb-r-place 0 -1 web-post-bl
           thumb-r-place 0.5 -1 thumb-post-br))

   ; diagonal

   (color ORA
          (hull
           (key-place 2 cornerrow (translate (wall-locate3 0 -0.5) web-post-bl))
           (key-place 2 cornerrow (translate (wall-locate1 0 0) web-post-bl))
           (key-place 1 cornerrow (translate (wall-locate1 0 0) web-post-br))

           )
          )

   (color CYA
          (hull
           (key-place 2 cornerrow (translate (wall-locate3 0 -1) web-post-br))
           (key-place 2 cornerrow web-post-br)
           (key-place 3 cornerrow web-post-bl)
           (key-place 3 cornerrow (translate (wall-locate3 0 -1) web-post-bl))
            )
          )


   ;(color-green
   ;  (wall-brace
   ;   thumb-r-place 1.3 -1 thumb-post-tr
   ;   (partial key-place 1 cornerrow) 2.1 -0.6 web-post-br))

   (color DGR
          (hull
           (key-place 2 cornerrow (translate (wall-locate2 0 -0.5) web-post-bl))
           (key-place 2 cornerrow (translate (wall-locate3 0 -1) web-post-br))

           (key-place 3 cornerrow (translate (wall-locate3 0 -1) web-post-bl))
           (thumb-r-place thumb-post-tr)

           (thumb-r-place (translate (wall-locate2 1.68 -0.6) thumb-post-tr))

           ;end hull
           ))

   ; thub right back corner
   (color-yellow
    (wall-brace thumb-r-place 1 0 web-post-br thumb-r-place 1.50 -0.6 thumb-post-tr))

   (wall-brace thumb-m-place 0 -1 web-post-br thumb-m-place 0 -1 web-post-bl)
   (wall-brace thumb-l-place 0 -1 web-post-br thumb-l-place 0 -1 web-post-bl)
   (wall-brace thumb-l-place 0 1 web-post-tr thumb-l-place 0 1 web-post-tl)
   (wall-brace thumb-l-place -1 0 web-post-tl thumb-l-place -1 0 web-post-bl)

   ; thumb corners
   (wall-brace thumb-l-place -1 0 web-post-bl thumb-l-place 0 -1 web-post-bl)
   (wall-brace thumb-l-place -1 0 web-post-tl thumb-l-place 0 1 web-post-tl)
   (wall-brace thumb-r-place 0.5 -1 web-post-br thumb-r-place 1 0 web-post-br)

   ; thumb tweeners
   (wall-brace thumb-r-place 0 -1 web-post-bl thumb-m-place 0 -1 web-post-br)
   (wall-brace thumb-m-place 0 -1 web-post-bl thumb-l-place 0 -1 web-post-br)
   ; clunky bit on the top left thumb connection  (normal connectors don't work well)

   ;; left wall
   (bottom-hull
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-l-place (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-l-place (translate (wall-locate3 -0.3 1) web-post-tr)))

   (hull
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-l-place (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-l-place (translate (wall-locate3 -0.3 1) web-post-tr))
    (thumb-m-place web-post-tl))

   (hull
    (left-key-place cornerrow -1 web-post)
    (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
    (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
    (thumb-m-place web-post-tl))


   (hull
    (left-key-place cornerrow -1 web-post)
    (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
    (key-place 0 cornerrow web-post-bl)
    (thumb-m-place web-post-tl))


   (hull
    (thumb-l-place web-post-tr)
    (thumb-l-place (translate (wall-locate1 -0.3 1) web-post-tr))
    (thumb-l-place (translate (wall-locate2 -0.3 1) web-post-tr))
    (thumb-l-place (translate (wall-locate3 -0.3 1) web-post-tr))
    (thumb-m-place web-post-tl))))
