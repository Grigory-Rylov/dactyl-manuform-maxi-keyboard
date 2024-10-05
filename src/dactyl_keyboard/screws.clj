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
   (screw-insert 0 0 bottom-radius top-radius height [6.5 3 controller-plate-height]) ; bottom left
   ; thumb
   (color-green
    (screw-insert 0 lastrow bottom-radius top-radius height [-6 -25 0]))

   ; top right
   (color-gray
    (screw-insert lastcol lastrow bottom-radius top-radius height [-12 3 0]))

   ; bottom right
   (color-gray (screw-insert lastcol 0 bottom-radius top-radius height [-1.1 0 0]))
   ; top
   (color-yellow
    (case thumbs-count
      0  (screw-insert 2 lastrow bottom-radius top-radius height [-6 -25 0])
      3  (screw-insert 2 lastrow bottom-radius top-radius height [-6 -25 0])
      5  (screw-insert 1 lastrow bottom-radius top-radius height [-4 -18 0])
      6  (screw-insert 0 lastrow bottom-radius top-radius height [10 -44 0])))

   ; bottom middle
   (color-red (screw-insert 2 0 bottom-radius top-radius height [-4 -3 0]))

   ; top left
   (color-blue
    (case thumbs-count
      0  (screw-insert lastcol lastrow bottom-radius top-radius height [-2 -6 0])
      3  (screw-insert lastcol lastrow bottom-radius top-radius height [-9 -14 0])
      5  (screw-insert 0 3 bottom-radius top-radius height [-1 20 controller-plate-height])
      6  (screw-insert 0 lastrow bottom-radius top-radius height [-4 1 0])))

   ; top middle
   (if (= thumbs-count 6)
     (screw-insert 2 lastrow bottom-radius top-radius height [-3 -5 0]))

   ; end union
   ))

(defn screw-insert-three-thumb-shapes-right [bottom-radius top-radius height should-reset-z]
  (union
   (if mono-mode
     (union
      (screw-insert 0 0 bottom-radius top-radius height [-30 -20 (if should-reset-z 0 controller-plate-height)]) ; bottom left controller-plate-height
      (screw-insert 0 0 bottom-radius top-radius height [-10 -64 (if should-reset-z 0 controller-plate-height)]) ; bottom left controller-plate-height
      ))

   (rotate [0, 0, (deg2rad board-z-angle)]
           (union
            ;left back
            (if mono-mode
              (screw-insert 0 0 bottom-radius top-radius height [10 6 (if should-reset-z 0 controller-plate-height)]) ; bottom left controller-plate-height

              (color-blue (screw-insert 0 0 bottom-radius top-radius height [7.5 5.5 (if should-reset-z 0 controller-plate-height)])) ; bottom left controller-plate-height
              )
            ; thumb
            (if trackball-mode
              (color-green (screw-insert 0 lastrow bottom-radius top-radius height [-38 17 0]))
              (color-green (screw-insert 0 lastrow bottom-radius top-radius height [-12 2 0]))
              )

            (if (= external-controller false)
              (color-blue (screw-insert 0 lastrow bottom-radius top-radius height [-2 43 (if should-reset-z 0 controller-plate-height)]))
              )

            ; bottom right
            (color-gray (screw-insert lastcol 0 bottom-radius top-radius height [4 -5 0]))
            ; top
            (color-yellow
             (screw-insert 2 lastrow bottom-radius top-radius height [-9 -4 0]))

            ; bottom middle
            (color-red (screw-insert 2 0 bottom-radius top-radius height [-4 -3 0]))

            ; front right
            (color-blue
             (screw-insert lastcol lastrow bottom-radius top-radius height [-5 6 0]))

            ; end union
            ))))

(defn screw-insert-three-thumb-shapes-left [bottom-radius top-radius height should-reset-z]
  (union
   (if mono-mode
     (union
      (screw-insert 0 0 bottom-radius top-radius height [-10 -20 (if should-reset-z 0 controller-plate-height)]) ; bottom left controller-plate-height
      (screw-insert 0 0 bottom-radius top-radius height [-10 -64 (if should-reset-z 0 controller-plate-height)]) ; bottom left controller-plate-height
      ))

   (rotate [0, 0, (deg2rad board-z-angle)]
           (union
            ;left back
            (color-blue (screw-insert 0 0 bottom-radius top-radius height [7.5 5.5 (if should-reset-z 0 controller-plate-height)])) ; bottom left controller-plate-height

            ; thumb
            (color-green (screw-insert 0 lastrow bottom-radius top-radius height [-12 2 0]))
            (if (= external-controller false)
              (color-blue (screw-insert 0 lastrow bottom-radius top-radius height [-2 43 (if should-reset-z 0 controller-plate-height)]))
              )

            ; back right
            (color-gray (screw-insert lastcol 0 bottom-radius top-radius height [4 -5 0]))
            ; top
            (color-yellow
             (screw-insert 2 lastrow bottom-radius top-radius height [-9 -4 0]))

            ; bottom middle
            (color-red (screw-insert 2 0 bottom-radius top-radius height [-4 -3 0]))

            ; front right
            (color-blue
             (screw-insert lastcol lastrow bottom-radius top-radius height [-5 6 0]))

            ; end union
            ))))

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

(defn screw-insert-all-shapes-right [bottom-radius top-radius height should-reset-z]
  (if externalThumb
    (screw-insert-0-thumb bottom-radius top-radius height)
    (case thumbs-count
      0 (screw-insert-six-shapes bottom-radius top-radius height)
      3 (screw-insert-three-thumb-shapes-right bottom-radius top-radius height should-reset-z)
      5 (screw-insert-six-shapes bottom-radius top-radius height)
      6 (screw-insert-six-shapes bottom-radius top-radius height))))

(defn screw-insert-all-shapes-left [bottom-radius top-radius height should-reset-z]
  (if externalThumb
    (screw-insert-0-thumb bottom-radius top-radius height)
    (case thumbs-count
      0 (screw-insert-six-shapes bottom-radius top-radius height)
      3 (screw-insert-three-thumb-shapes-left bottom-radius top-radius height should-reset-z)
      5 (screw-insert-six-shapes bottom-radius top-radius height)
      6 (screw-insert-six-shapes bottom-radius top-radius height))))

; Hole Depth Y: 4.4
(def screw-insert-height 4)

; Hole Diameter C: 4.1-4.4
(def screw-insert-bottom-radius (/ 4.4 2))
(def screw-insert-top-radius (/ 4.4 2))
(def screw-insert-holes-right
  (screw-insert-all-shapes-right screw-insert-bottom-radius screw-insert-top-radius screw-insert-height false))
(def screw-insert-holes-left
  (screw-insert-all-shapes-left screw-insert-bottom-radius screw-insert-top-radius screw-insert-height false))

; Wall Thickness W:\t1.65
(def screw-insert-outers-right
  (screw-insert-all-shapes-right (+ screw-insert-bottom-radius 1.65) (+ screw-insert-top-radius 1.65) (+ screw-insert-height 1.5) false))

(def screw-insert-outers-left
  (screw-insert-all-shapes-left (+ screw-insert-bottom-radius 1.65) (+ screw-insert-top-radius 1.65) (+ screw-insert-height 1.5) false))

(def screw-insert-outers-for-plate-right
  (screw-insert-all-shapes-right (+ screw-insert-bottom-radius 1.65) (+ screw-insert-top-radius 1.65) (+ screw-insert-height 1.5) true))
(def screw-insert-outers-for-plate-left
  (screw-insert-all-shapes-left (+ screw-insert-bottom-radius 1.65) (+ screw-insert-top-radius 1.65) (+ screw-insert-height 1.5) true))

(def screw-head-height 1.65)
(def plate-total-height (+ plate-border-height plate-height))

(def screw-insert-screw-holes-right
  (union
   (translate [0, 0, (- plate-total-height screw-head-height)]
              (screw-insert-all-shapes-right 1.7 1.7 (- plate-total-height screw-head-height) false))

   (translate [0, 0, -0.1] (screw-insert-all-shapes-right 2.75 1.7 (+ screw-head-height 0.2) false))))

(def screw-insert-screw-holes-left
  (union
   (translate [0, 0, (- plate-total-height screw-head-height)]
              (screw-insert-all-shapes-left 1.7 1.7 (- plate-total-height screw-head-height) false))

   (translate [0, 0, -0.1] (screw-insert-all-shapes-left 2.75 1.7 (+ screw-head-height 0.2) false))))

(def screw-insert-screw-holes-for-plate-right
  (union
   (translate [0, 0, (- plate-total-height screw-head-height)]
              (screw-insert-all-shapes-right 1.7 1.7 (- plate-total-height screw-head-height) true))

   (translate [0, 0, -0.1] (screw-insert-all-shapes-right 2.75 1.7 (+ screw-head-height 0.2) true))))

(def screw-insert-screw-holes-for-plate-left
  (union
   (translate [0, 0, (- plate-total-height screw-head-height)]
              (screw-insert-all-shapes-left 1.7 1.7 (- plate-total-height screw-head-height) true))

   (translate [0, 0, -0.1] (screw-insert-all-shapes-left 2.75 1.7 (+ screw-head-height 0.2) true))))
