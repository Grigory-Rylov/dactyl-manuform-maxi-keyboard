(ns dactyl-keyboard.case
  (:refer-clojure :exclude [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.config :refer :all]))


;;;;;;;;;;
;; Case ;;
;;;;;;;;;;

(defn bottom [height p]
  (->> (project p)
       (extrude-linear {:height height :twist 0 :convexity 0})
       (translate [0 0 (- (/ height 2) 10)])))

(defn bottom-hull [& p]
  (hull p (bottom 0.001 p)))

(def left-wall-x-offset 5) ; original 10
(def left-wall-z-offset  3) ; original 3

(defn left-key-position [row direction]
  (map - (key-position 0 row [(* mount-width -0.5) (* direction mount-height 0.5) 0]) [left-wall-x-offset 0 left-wall-z-offset]))

(defn left-key-place [row direction shape]
  (translate (left-key-position row direction) shape))

(defn wall-locate1 [dx dy] [(* dx wall-thickness) (* dy wall-thickness) -1])
(defn wall-locate2 [dx dy] [(* dx wall-xy-offset) (* dy wall-xy-offset) wall-z-offset])
(defn wall-locate3 [dx dy] [(* dx (+ wall-xy-offset wall-thickness)) (* dy (+ wall-xy-offset wall-thickness)) wall-z-offset])

(defn wall-brace [place1 dx1 dy1 post1 place2 dx2 dy2 post2]
  (union
   (hull
    (place1 post1)
    (place1 (translate (wall-locate1 dx1 dy1) post1))
    (place1 (translate (wall-locate2 dx1 dy1) post1))
    (place1 (translate (wall-locate3 dx1 dy1) post1))
    (place2 post2)
    (place2 (translate (wall-locate1 dx2 dy2) post2))
    (place2 (translate (wall-locate2 dx2 dy2) post2))
    (place2 (translate (wall-locate3 dx2 dy2) post2)))
   (bottom-hull
    (place1 (translate (wall-locate2 dx1 dy1) post1))
    (place1 (translate (wall-locate3 dx1 dy1) post1))
    (place2 (translate (wall-locate2 dx2 dy2) post2))
    (place2 (translate (wall-locate3 dx2 dy2) post2)))))

(defn key-wall-brace [x1 y1 dx1 dy1 post1 x2 y2 dx2 dy2 post2]
  (wall-brace (partial key-place x1 y1) dx1 dy1 post1
              (partial key-place x2 y2) dx2 dy2 post2))

(def right-wall
  (let [tr (if (true? pinky-15u) wide-post-tr web-post-tr)
        br (if (true? pinky-15u) wide-post-br web-post-br)]
    (union (key-wall-brace lastcol 0 0 1 tr lastcol 0 1 0 tr)
           (for [y (range 0 nrows)] (key-wall-brace lastcol y 1 0 tr lastcol y 1 0 br))
           (for [y (range 0 (dec nrows))] (key-wall-brace lastcol y 1 0 br lastcol (inc y) 1 0 tr))
           (key-wall-brace lastcol cornerrow 0 -1 br lastcol cornerrow 1 0 br))))


(def case-walls
  (union
   right-wall
   ; back wall
   (for [x (range 0 ncols)] (if (not= x 1) (key-wall-brace x 0 0 1 web-post-tl x       0 0 1 web-post-tr)))
   (for [x (range 1 ncols)] (if (not= x 2)(key-wall-brace x 0 0 1 web-post-tl (dec x) 0 0 1 web-post-tr)))
   (key-wall-brace 2 0 0 1 web-post-tl 2       0 -0.2 1 web-post-tr)
   (key-wall-brace 2 0 0.0 1 web-post-tl (dec 2) 0 -0.2 1 web-post-tr)
   (key-wall-brace 1 0 0 1 web-post-tl 1       0 -0.2 1 web-post-tr)

   ; left wall
   (for [y (range 0 nrows)]
                               (union (wall-brace (partial left-key-place y 1) -1 0 web-post (partial left-key-place y -1) -1 0 web-post)
                                     (hull (key-place 0 y web-post-tl)
                                           (key-place 0 y web-post-bl)
                                           (left-key-place y  1 web-post)
                                           (left-key-place y -1 web-post))))

   (for [y (range 0 (dec nrows))] (union (wall-brace (partial left-key-place  y -1) -1 0 web-post (partial left-key-place (inc y)  1) -1 0 web-post)
                                     (hull (key-place 0 (inc y)       web-post-tl)
                                           (key-place 0 y web-post-bl)
                                           (left-key-place (inc y)        1 web-post)
                                           (left-key-place  y -1 web-post))))
   (wall-brace (partial key-place 0 0) 0 1 web-post-tl (partial left-key-place 0 1) 0 1 web-post)
   (wall-brace (partial left-key-place 0 1) 0 1 web-post (partial left-key-place 0 1) -1 0 web-post)
   ; front wall
   (key-wall-brace 3 lastrow 0 -1 web-post-bl 2 lastrow 0 -1 web-post-br)

   (for [x (range 3 ncols)] (key-wall-brace x cornerrow 0 -1 web-post-bl x cornerrow 0 -1 web-post-br))
   (for [x (range 3 ncols)] (key-wall-brace x cornerrow 0 -1 web-post-bl (dec x) cornerrow 0 -1 web-post-br))

   ;wall between key 1 and thumb
   (key-wall-brace 2 cornerrow 1 -1 web-post-bl 2 cornerrow 0 -1 web-post-br)

   (wall-brace
      (partial key-place 1 cornerrow) 1 0 web-post-br
      (partial key-place 2 cornerrow) 1 -1 web-post-bl)

   ; thumb walls
   (wall-brace
      thumb-r-place  0 -1 web-post-bl
      thumb-r-place  0.5 -1 thumb-post-br)

   (wall-brace
      thumb-r-place  1 -1 thumb-post-tr
      (partial key-place 1 cornerrow) 1 0 web-post-br)
   (wall-brace thumb-r-place  1 0 web-post-br thumb-r-place  1 -1 thumb-post-tr)
   (wall-brace thumb-m-place  0 -1 web-post-br thumb-m-place  0 -1 web-post-bl)
   (wall-brace thumb-l-place  0 -1 web-post-br thumb-l-place  0 -1 web-post-bl)
   (wall-brace thumb-l-place  0  1 web-post-tr thumb-l-place  0  1 web-post-tl)
   (wall-brace thumb-l-place -1  0 web-post-tl thumb-l-place -1  0 web-post-bl)

   ; thumb corners
   (wall-brace thumb-l-place -1  0 web-post-bl thumb-l-place  0 -1 web-post-bl)
   (wall-brace thumb-l-place -1  0 web-post-tl thumb-l-place  0  1 web-post-tl)
   (wall-brace thumb-r-place 0.5  -1 web-post-br thumb-r-place  1  0 web-post-br)

   ; thumb tweeners
   (wall-brace thumb-r-place  0 -1 web-post-bl thumb-m-place  0 -1 web-post-br)
   (wall-brace thumb-m-place  0 -1 web-post-bl thumb-l-place  0 -1 web-post-br)
   ; clunky bit on the top left thumb connection  (normal connectors don't work well)

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
    (thumb-m-place web-post-tl)))

  )
