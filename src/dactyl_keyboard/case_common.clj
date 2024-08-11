(ns dactyl-keyboard.case-common
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.connectors-common :refer :all]
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

(def left-wall-x-offset 5)

; original 10
(def left-wall-z-offset 3)

; original 3

(defn left-key-position [row direction]
  (map - (key-position 0 row [(* mount-width -0.5) (* direction mount-height 0.5) 0])
       [left-wall-x-offset 0 left-wall-z-offset]))

(defn left-key-place [row direction shape]
  (rotate [0, 0, (deg2rad board-z-angle)]
          (translate (left-key-position row direction) shape)))

(defn wall-locate1 [dx dy] [(* dx wall-thickness) (* dy wall-thickness) -1])
(defn wall-locate-mid [dx dy dz] [(* dx wall-thickness) (* dy wall-thickness) (* dz wall-thickness)])

(defn wall-locate2 [dx dy]
  [(* dx wall-xy-offset) (* dy wall-xy-offset) wall-z-offset])
(defn wall-locate3 [dx dy]
  [(* dx (+ wall-xy-offset wall-thickness0))
   (* dy (+ wall-xy-offset wall-thickness0))
   wall-z-offset])

(defn wall-locate22 [dx dy] [0 0 0])

(defn wall-rotation [dx dy] [0 0 (deg2rad board-z-angle)])

(defn wall-locate32 [dx dy]
  [(* dx (+ 10 wall-thickness0))
   (* dy (+ 10 wall-thickness0))
   wall-z-offset])

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

(defn wall-brace-top [place1 dx1 dy1 post1 place2 dx2 dy2 post2]
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

   (color-red
    (hull
     (hull
      (place1 (translate (wall-locate2 dx1 dy1) post1))
      (place1 (translate (wall-locate3 dx1 dy1) post1))
      (place2 (translate (wall-locate2 dx2 dy2) post2))
      (place2 (translate (wall-locate3 dx2 dy2) post2)))

     (rotate [0,0, (deg2rad 2)] (translate [-66, 0, 0]
                (hull
                 (place1 (translate (wall-locate2 0 0) post1))
                 (place1 (translate (wall-locate3 0 0) post1))
                 (place2 (translate (wall-locate2 0 0) post2))
                 (place2 (translate (wall-locate3 0 0) post2)))))))))

(defn key-wall-brace [x1 y1 dx1 dy1 post1 x2 y2 dx2 dy2 post2]
  (wall-brace (partial key-place x1 y1) dx1 dy1 post1
              (partial key-place x2 y2) dx2 dy2 post2))


(def right-wall-extra-middle-row
  (let [tr (if (true? pinky-15u) wide-post-tr web-post-tr)
        br (if (true? pinky-15u) wide-post-br web-post-br)]
    (union (key-wall-brace lastcol 0 0 1 tr lastcol 0 1 0 tr)
           (for [y (range 0 lastrow)] (key-wall-brace lastcol y 1 0 tr lastcol y 1 0 br))
           (for [y (range 1 lastrow)] (key-wall-brace lastcol (dec y) 1 0 br lastcol y 1 0 tr))
           (key-wall-brace lastcol cornerrow 0 -1 br lastcol cornerrow 1 0 br))))


(def right-wall-no-extra-row
  (let [tr  web-post-tr
        br  web-post-br]
    (union
     (key-wall-brace lastcol 0 0 1 web-post-tr lastcol 0 1 0 web-post-tr)
     (for [y (range 0 nrows)] (key-wall-brace lastcol y 1 0 tr lastcol y 1 0 br))
     (for [y (range 0 (dec nrows))]
       (key-wall-brace lastcol y 1 0 br lastcol (inc y) 1 0 tr))

     (key-wall-brace lastcol cornerrow 0 -1 web-post-br lastcol cornerrow 1 0 web-post-br))))

(def right-wall
  (if extra-middle-row right-wall-extra-middle-row right-wall-no-extra-row))

(def offset-wall-x -1.0)
(def left-wall
  (let [tr  web-post-tl
        br  web-post-bl]
    (union (key-wall-brace 0 0 0 1 web-post-tl 0 0 offset-wall-x 0 web-post-tl)
           (for [y (range 0 nrows)]
             (key-wall-brace 0 y offset-wall-x 0 tr 0 y offset-wall-x 0 br))
           (for [y (range 0 (dec nrows))]
             (key-wall-brace 0 y offset-wall-x 0 br 0 (inc y) offset-wall-x 0 tr))
           (key-wall-brace 0 cornerrow 0 -1 web-post-bl 0 cornerrow offset-wall-x 0 web-post-bl))))
