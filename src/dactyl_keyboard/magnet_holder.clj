(ns dactyl-keyboard.magnet-holder
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.case-common :refer :all]
            [dactyl-keyboard.config :refer :all]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Magnet wrist rest  holder;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn shape-insert [column row offset shape]
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
    (->> shape (translate (map + offset [(first position) (second position) 0])))))

(defn magnet-shape-insert [column row offset shape]
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

    (->> shape
         (translate
          (map + offset
               [(first position) (second position) (+ (/ magnet-diameter 2) magnet-wall-width)])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; wrist rest magnetic holder mounting ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn magnet-hole  [radius inner-radius height]
  (rotate [0 (deg2rad 90) (deg2rad 90)]
          (union
           (binding [*fn* 100] (cylinder radius height))
           (binding [*fn* 100] (cylinder inner-radius (+ height 10))))))

(defn magnet-stiffness-booster  [side height]
  (rotate [0 (deg2rad 90) (deg2rad 90)]
          (cube side side height)))

; keyboard's magnet hole
(def connectionLeftOffset1 -6.5)
(def connectionLeftOffset2 -16)

(def connectionYOffset1 18.7)
(def connectionYOffset2 4.3)

(def magnet-place
  (let [col1 3
        col2 4]
    (rotate [0, 0, (deg2rad board-z-angle)]
            (union
             (rotate [0, 0, (angle-for-column-z col2)]
                     (magnet-shape-insert col2, lastrow, [connectionLeftOffset2 connectionYOffset2 0] (magnet-hole (+ (/ magnet-diameter 2) 0.1) (/ magnet-inner-diameter 2) magnet-height)))
             (rotate [0, 0, (angle-for-column-z col1)]
                     (magnet-shape-insert col1, lastrow, [connectionLeftOffset1 connectionYOffset1 0] (magnet-hole (+ (/ magnet-diameter 2) 0.1) (/ magnet-inner-diameter 2) magnet-height)))
   ))
  )
)

(def magnet-stiffness-booster
  (let [col1 3
        col2 4]

    (rotate [0, 0, (deg2rad board-z-angle)]
            (union
             (rotate [0, 0, (angle-for-column-z col2)]
                     (magnet-shape-insert col2, lastrow, [connectionLeftOffset2 (+ connectionYOffset2 wall-thickness) 0]
                                          (magnet-stiffness-booster (+ magnet-diameter 2) magnet-booster-width)))

             (rotate [0, 0, (angle-for-column-z col1)]
                     (magnet-shape-insert col1, lastrow, [connectionLeftOffset1 (+ connectionYOffset1 wall-thickness) 0]
                                          (magnet-stiffness-booster (+ magnet-diameter 2) magnet-booster-width))))
            ; end rotate
            )))

; wrist rest magnet holder
(def magnet-connector
  (difference
   (rotate [0 (deg2rad 90) (deg2rad 90)]
           (translate [0 0 (/ magnet-connector-length -2)]
                      (binding [*fn* 100]
                        (cylinder (+ (/ magnet-diameter 2) magnet-connector-wall-width) magnet-connector-length))))
   (magnet-hole (+ (/ magnet-diameter 2) 0.1) (/ magnet-inner-diameter 2) magnet-height)))

(def magnet-connector-offset 1)
(def magnet-connectors
  (union
   (magnet-shape-insert 4, lastrow, [0 (- -13 (+ magnet-connector-offset (/ magnet-height 2))) 0] magnet-connector)
   (magnet-shape-insert 3, lastrow, [0 (- 0.75 (+ magnet-connector-offset (/ magnet-height 2))) 0] magnet-connector)))
