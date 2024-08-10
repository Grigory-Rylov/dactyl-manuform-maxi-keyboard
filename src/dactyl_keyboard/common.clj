(ns dactyl-keyboard.common
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.config :refer :all]))


;;;;;;;;;;;;;;;;;;;;;;;;;
;; Placement Functions ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def columns (range 0 ncols))
(def rows (range 0 nrows))

(def cap-top-height (+ plate-thickness sa-profile-key-height))
(def row-radius
  (+
   (/ (/ (+ mount-height extra-height) 2)
      (Math/sin (/ column-curvature 2)))
   cap-top-height))

(def column-radius
  (+
   (/ (/ (+ mount-width extra-width) 2)
      (Math/sin (/ row-curvature 2)))
   cap-top-height))

(def column-x-delta (+ -1 (- (* column-radius (Math/sin row-curvature)))))

(defn offset-for-column [column]
  (cond
    (= column 0) 0
    (= column 1) 0
    ; pinky finger1
    (= column 2) 0
    ; pinky finger2
    (= column 3) 0
    ; index finger1
    (= column 4) 0

    (= column 5) 0
    ; index finger2
    :else        0))

(defn non-orto-offset-for-column [column]
  (cond
    ; index
    (= column 0) -1.5
    (= column 1) -0.5
    ;middle
    (= column 2) 0
    ; ring
    (= column 3) 0.5
    ; pinky
    (= column 4) 3
    (= column 5) 4
    :else        0))

(def column-0-z-angle 4)
(def column-1-z-angle 2)

(defn angle-for-column-z [column]
  (cond
    (= column 0) (deg2rad column-0-z-angle)
    (= column 1) (deg2rad column-1-z-angle)
    (= column 2) (deg2rad 0)
    (= column 3) (deg2rad -2)
    (= column 4) (deg2rad -6)
    (= column 5) (deg2rad -8)
    :else        (deg2rad 0)))

(defn angle-for-column-y [column]
  (cond
    (= column 0) (deg2rad 4)
    (= column 1) (deg2rad 2)
    (= column 2) (deg2rad 0)
    (= column 3) (deg2rad 0)
    (= column 4) (deg2rad 0)
    (= column 5) (deg2rad 0)
    :else        (deg2rad 0)))

(defn apply-key-geometry [translate-fn rotate-x-fn rotate-y-fn column row shape]
  (let [column-angle       (* row-curvature (- centercol column))
        placed-shape       (->> shape
                                (translate-fn [(offset-for-column column) 0 (- row-radius)])
                                (rotate-x-fn (* column-curvature (- centerrow row)))
                                (translate-fn [0 0 row-radius])
                                (translate-fn [0 0 (- column-radius)])
                                (rotate-y-fn column-angle)
                                (translate-fn [0 0 column-radius])
                                (translate-fn (column-offset column)))
        column-z-delta     (* column-radius (- 1 (Math/cos column-angle)))
        placed-shape-ortho (->> shape
                                (translate-fn [0 0 (- row-radius)])
                                (rotate-x-fn (* column-curvature (- centerrow row)))
                                (translate-fn [0 0 row-radius])
                                (rotate-y-fn column-angle)
                                (translate-fn [(- (* (- column centercol) column-x-delta)) 0 column-z-delta])
                                (translate-fn (column-offset column)))
        placed-shape-fixed (->> shape
                                (rotate-y-fn (nth fixed-angles column))
                                (translate-fn [(nth fixed-x column) 0 (nth fixed-z column)])
                                (translate-fn [0 0 (- (+ row-radius (nth fixed-z column)))])
                                (rotate-x-fn (* column-curvature (- centerrow row)))
                                (translate-fn [0 0 (+ row-radius (nth fixed-z column))])
                                (rotate-y-fn fixed-tenting)
                                (translate-fn [0 (second (column-offset column)) 0]))]
    (->>
     (case column-style
       :orthographic placed-shape-ortho
       :fixed        placed-shape-fixed
       placed-shape)
     (rotate-y-fn tenting-angle)
     (translate-fn [0 0 keyboard-z-offset]))))


(defn apply-key-geometry2 [translate-fn rotate-x-fn rotate-y-fn rotate-z-fn column row shape]
  (let [column-angle       (* row-curvature (- centercol column))

        placed-shape       (->> shape
                                (translate-fn [(+ (non-orto-offset-for-column column) 0) 0 (- row-radius)]) ; offsets for key
                                (rotate-x-fn (* column-curvature (- centerrow row)))
                                (translate-fn [0 0 row-radius])
                                (translate-fn [0 0 (- column-radius)])
                                (rotate-y-fn column-angle)
                                (translate-fn [0 0 column-radius])
                                (translate-fn (column-offset column))
                                (rotate-z-fn (angle-for-column-z column)))

        column-z-delta     (* column-radius (- 1 (Math/cos column-angle)))
        placed-shape-ortho (->> shape
                                (translate-fn [0 0 (- row-radius)])
                                (rotate-x-fn (* column-curvature (- centerrow row)))
                                (translate-fn [0 0 row-radius])
                                (rotate-y-fn column-angle)
                                (translate-fn [(- (* (- column centercol) column-x-delta)) 0 column-z-delta])
                                (translate-fn (column-offset column)))
        placed-shape-fixed (->> shape
                                (rotate-y-fn (nth fixed-angles column))
                                (translate-fn [(nth fixed-x column) 0 (nth fixed-z column)])
                                (translate-fn [0 0 (- (+ row-radius (nth fixed-z column)))])
                                (rotate-x-fn (* column-curvature (- centerrow row)))
                                (translate-fn [0 0 (+ row-radius (nth fixed-z column))])
                                (rotate-y-fn fixed-tenting)
                                (translate-fn [0 (second (column-offset column)) 0]))]
    (->>
     (case column-style
       :orthographic placed-shape-ortho
       :fixed        placed-shape-fixed
       placed-shape)
     (rotate-y-fn tenting-angle)
     (translate-fn [0 0 keyboard-z-offset]))))

(defn key-place [column row shape]
  (rotate [0, 0, (deg2rad board-z-angle)]
          (if cols-angle
            ; not orto
            (apply-key-geometry2 translate
                                 (fn [angle obj] (rotate angle [1 0 0] obj))
                                 (fn [angle obj] (rotate angle [0 1 0] obj))
                                 (fn [angle obj] (rotate angle [0 0 1] obj))

                                 column row shape)
            ; else orto
            (apply-key-geometry translate
                                (fn [angle obj] (rotate angle [1 0 0] obj))
                                (fn [angle obj] (rotate angle [0 1 0] obj))
                                column row shape))))


(defn rotate-around-x [angle position]
  (mmul
   [[1 0 0]
    [0 (Math/cos angle) (- (Math/sin angle))]
    [0 (Math/sin angle) (Math/cos angle)]]
   position))

(defn rotate-around-y [angle position]
  (mmul
   [[(Math/cos angle) 0 (Math/sin angle)]
    [0 1 0]
    [(- (Math/sin angle)) 0 (Math/cos angle)]]
   position))

(defn key-position [column row position]
  (apply-key-geometry (partial map +) rotate-around-x rotate-around-y column row position))

(defn color-green [shape] (color [0 1 0 1] shape))
(defn color-red [shape] (color [1 0 0 1] shape))
(defn color-blue [shape] (color [0 0 1 1] shape))
(defn color-yellow [shape] (color [1 1 0 1] shape))
(defn color-white [shape] (color [1 1 1 1] shape))
(defn color-gray [shape] (color [0.5 0.5 0.5 1] shape))
