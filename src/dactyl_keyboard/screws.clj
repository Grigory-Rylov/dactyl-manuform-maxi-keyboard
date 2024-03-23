(ns dactyl-keyboard.screws
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.config :refer :all]
            [dactyl-keyboard.case-common :refer :all]))

;;;;;;;;;;;
;; SCREW ;;
;;;;;;;;;;;

(defn screw-insert-shape [bottom-radius top-radius height]
  (union
   (->>
    (binding [*fn* 30]
      (cylinder [bottom-radius top-radius] height)))
   (translate [0 0 (/ height 2)] (->> (binding [*fn* 30] (sphere top-radius))))))

(defn screw-insert [column row bottom-radius top-radius height offset]
  (let [shift-right   (= column lastcol)
        shift-left    (= column 0)
        shift-up      (and (not (or shift-right shift-left)) (= row 0))
        shift-down    (and (not (or shift-right shift-left)) (>= row lastrow))
        position      (if shift-up
                        (key-position column row (map + (wall-locate2 0 1) [0 (/ mount-height 2) 0]))
                        (if shift-down
                          (key-position column row (map - (wall-locate2 0 -1) [0 (/ mount-height 2) 0]))
                          (if shift-left
                            (map + (left-key-position row 0) (wall-locate3 -1 0))
                            (key-position column row (map + (wall-locate2 1 0) [(/ mount-width 2) 0 0])))))]
    (->> (screw-insert-shape bottom-radius top-radius height)
         (translate (map + offset [(first position) (second position) (/ height 2)])))))

(defn screw-insert-six-shapes [bottom-radius top-radius height]
  (union
   (screw-insert 0 0 bottom-radius top-radius height [6 7 0]) ; bottom left
   ; thumb
   (color-green
    (screw-insert 0 lastrow bottom-radius top-radius height [-3 -33 0]))

   ; top right


   ; bottom right
   (color-gray (screw-insert lastcol 0 bottom-radius top-radius height [-1.1 0 0]))
   ; top
   (color-yellow
    (case thumbs-count
      0  (screw-insert 2 lastrow bottom-radius top-radius height [-6 -25 0])
      3  (screw-insert 2 lastrow bottom-radius top-radius height [-6 -25 0])
      5  (screw-insert 0 lastrow bottom-radius top-radius height [14 -42 0])
      6  (screw-insert 0 lastrow bottom-radius top-radius height [10 -44 0])))

   ; bottom middle
   (color-red (screw-insert 2 0 bottom-radius top-radius height [-6 -2 0]))

   ; top left
   (color-blue
   (case thumbs-count
     0  (screw-insert lastcol lastrow bottom-radius top-radius height [-2 -6 0])
     3  (screw-insert lastcol lastrow bottom-radius top-radius height [-9 -14 0])
     5  (screw-insert 0 lastrow bottom-radius top-radius height [-2 5 0])
     6  (screw-insert 0 lastrow bottom-radius top-radius height [-4 1 0])))

   ; top middle
   (if (= thumbs-count 6)
     (screw-insert 2 lastrow bottom-radius top-radius height [-3 -5 0]))

   ; end union
   ))

(defn screw-insert-0-thumb [bottom-radius top-radius height]
  (union
   ; bottom left
   (screw-insert 0 0 bottom-radius top-radius height [9 7 0])
   ; thumb
   (color-green (screw-insert 0 lastrow bottom-radius top-radius height [15 -12 0]))

   ; top right
   (color-blue
    (screw-insert lastcol lastrow bottom-radius top-radius height [-2 -6 0]))

   ; bottom right
   (color-gray (screw-insert lastcol 0 bottom-radius top-radius height [-2 5 0]))
   ; top
   ;   (color-yellow
   ;     (screw-insert 2 lastrow bottom-radius top-radius height [-6 2 0]))

   ; bottom middle
   (color-red (screw-insert 3 0 bottom-radius top-radius height [-7 -2.5 0]))

   ; top left
   (screw-insert lastcol lastrow bottom-radius top-radius height [-2 -6 0])

   ; end union
   ))

(defn screw-insert-all-shapes [bottom-radius top-radius height]
  (if externalThumb
    (screw-insert-0-thumb bottom-radius top-radius height)
    (screw-insert-six-shapes bottom-radius top-radius height)
   ))

; Hole Depth Y: 4.4
(def screw-insert-height 4)

; Hole Diameter C: 4.1-4.4
(def screw-insert-bottom-radius (/ 4.4 2))
(def screw-insert-top-radius (/ 4.4 2))
(def screw-insert-holes
  (screw-insert-all-shapes screw-insert-bottom-radius screw-insert-top-radius screw-insert-height))

; Wall Thickness W:\t1.65
(def screw-insert-outers
  (screw-insert-all-shapes (+ screw-insert-bottom-radius 1.65) (+ screw-insert-top-radius 1.65) (+ screw-insert-height 1.5)))

(def screw-head-height 1.65)
(def plate-total-height (+ plate-border-height plate-height))
(def screw-insert-screw-holes
  (union
   (translate [0, 0, (- plate-total-height screw-head-height)]
              (screw-insert-all-shapes 1.7 1.7 (- plate-total-height screw-head-height)))
   (translate [0, 0, -0.1] (screw-insert-all-shapes 2.75 1.7 (+ screw-head-height 0.2)))))
