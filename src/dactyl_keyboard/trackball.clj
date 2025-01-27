(ns dactyl-keyboard.trackball
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.config :refer :all]
            [dactyl-keyboard.3-thumbs-case :refer :all]))

(def lock-cylinder
  (let [radius 0.75
        depth  4]
    (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder radius depth)))))

(def lock-cylinder-inner
  (let [radius 2
        depth  1.3]
    (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder radius depth)))))


(def lock-hole
  (let [radius 0.75
        height 2]
    (union
     (hull
      lock-cylinder
      (translate [0, 0, (* height -1)] lock-cylinder)
      ; hull
      )

     (hull
      lock-cylinder-inner
      (translate [0.2, 0, (* height -1)] lock-cylinder-inner)
      (translate [0.2, 1.5, 0] lock-cylinder-inner)
      (translate [0.2, 1.5, (* height -1)] lock-cylinder-inner)
      ; hull
      )

     ; horizontal
     (color-yellow
      (translate [0, 0, (* height -1)]
                 (hull
                  lock-cylinder
                  (translate [0, 1.5, 0] lock-cylinder))))


     ; end union
     )))

(def sensor-hole
  (let [radius             7.7
        lense-inne-width   15.5
        lense-inner-height 14
        lense-inner-depth  7
        depth              4
        width              (- 23 (* radius 2))
        height             (- 20 (* radius 2))]

    (union
     (translate [0, 0, (* depth -1)]
                (cube lense-inne-width lense-inner-height lense-inner-depth))

     (minkowski
      (cube width height depth)
      (cylinder 7.7 1)))))

(defn place_bearing [radius zAngle yAngle object]
  (rotate [0, (deg2rad yAngle), (deg2rad zAngle)] (translate [radius, 0, 0] object)))

(def lense-holder
  (cube 27 7 6))

(def controller-holder
  (let [radius      3
        hole-radius 1
        distance    trackball-mount-distance
        height      20]
    (difference
     (union
      (translate [0, (/ distance 2), -15] (binding [*fn* 20] (cylinder radius height)))
      (translate [0, (/ distance -2), -15] (binding [*fn* 20] (cylinder radius height)))
      ; end union
      )
     (translate [0, (/ distance 2), -25] (binding [*fn* 20] (cylinder hole-radius height)))
     (translate [0, (/ distance -2), -25] (binding [*fn* 20] (cylinder hole-radius height)))
     ; end difference
     )))

(def trackball-case
  (let [ball_radius          trackball-ball-radius
        bearing_radius       trackball-bearing-radius
        ball_hole_radius     (+ ball_radius bearing_radius)
        ball_place_wall      trackball-place-wall
        ball_place_radius    (+ ball_hole_radius ball_place_wall)
        bearing_place_radius (+ ball_radius bearing_radius)]

    (difference
     ; outer sphere
     (union

      (intersection
       (binding [*fn* trackball-fn] (sphere ball_place_radius))

       (union
        ;outer shpere
        (difference
         (binding [*fn* trackball-fn] (sphere ball_place_radius))
         ; top hole
         (translate [0, 0, (+ (/ ball_place_radius 2) 2)]
                    (cube (* ball_place_radius 2) (* ball_place_radius 2) ball_place_radius))
         ; end difference
         )
        (scale [1, 0.5, 0.30] (binding [*fn* trackball-fn] (sphere (+ ball_hole_radius 10))))
        ; end intersection
        ))

      ;(translate [0, 0, -23] lense-holder)
      controller-holder)

     ; inner ball hole
     (binding [*fn* trackball-fn] (sphere ball_hole_radius))

     (translate [0, 0, -23] (rotate [0, 0, (deg2rad 90)] sensor-hole))
     ;(translate [0, -22.8, 0] lock-hole)
     ;(translate [0, 22.8, 0] (rotate [0, 0, (deg2rad 180)] lock-hole))

     ;bearing place
     (place_bearing bearing_place_radius 0, 30 (binding [*fn* trackball-fn] (sphere bearing_radius)))
     (place_bearing bearing_place_radius 120, 30 (binding [*fn* trackball-fn] (sphere bearing_radius)))
     (place_bearing bearing_place_radius -120, 30 (binding [*fn* trackball-fn] (sphere bearing_radius)))

     ; end difference
     )
    ; end let
    )
  ; end def
  )

(def trackball-inner-hole
  (let [ball_radius          trackball-ball-radius
        bearing_radius       trackball-bearing-radius
        ball_hole_radius     (+ ball_radius bearing_radius)
        ball_place_wall      trackball-place-wall
        ball_place_radius    (+ ball_hole_radius ball_place_wall)
        bearing_place_radius (+ ball_radius bearing_radius)
        height               20
        holder-diameter      (+ trackball-mount-distance trackball-mount-diameter 4)]
    (union
     (binding [*fn* trackball-fn] (sphere ball_place_radius))
     ;(translate [0, 0, -23] lense-holder)
     (translate [0 0, -15]
                (binding [*fn* trackball-fn] (cylinder (/ holder-diameter 2) (+ height 6))))

     ;end union
     )))

(def trackball-walls
  (let [ball_radius                 trackball-ball-radius
        bearing_radius              trackball-bearing-radius
        ball_hole_radius            (+ ball_radius bearing_radius)
        ball_place_wall             trackball-place-wall
        ball_place_radius           (+ ball_hole_radius ball_place_wall)
        bearing_place_radius        (+ ball_radius bearing_radius)
        height                      57
        bottom_height               47
        top_height                  10
        wall                        2
        height-offset               2
        holder-diameter             (+ trackball-mount-distance trackball-mount-diameter 4)]
    (difference
     (union
      ;top cylinder

      (difference
       (color PIN
              (translate [0 0, (- (/ top_height -2) height-offset)]
                         (binding [*fn* trackball-fn] (cylinder ball_place_radius top_height))))

       (rotate [(deg2rad 0) (deg2rad -35) (deg2rad 6)] (cube 200 200 31))

       ;end difference top ring
       )
      ;bottom cylinder
      (color PUR
             (translate [0 0, (- (/ bottom_height -2) height-offset top_height)]
                        (binding [*fn* trackball-fn] (cylinder ball_place_radius bottom_height)))))
     (translate [0 0, (/ height -2)]
                (binding [*fn* trackball-fn] (cylinder (- ball_place_radius wall) height)))
     (color CYA (translate [18 6, -44] (cube 28 31 50)))

     ;end union
     )))

(def trackball-hole
  (let [ball_radius          trackball-ball-radius
        bearing_radius       trackball-bearing-radius
        ball_hole_radius     (+ ball_radius bearing_radius)
        ball_place_wall      trackball-place-wall
        ball_place_radius    (+ ball_hole_radius ball_place_wall)
        bearing_place_radius (+ ball_radius bearing_radius)
        height               75
        wall                 2
        holder-diameter      (+ trackball-mount-distance trackball-mount-diameter 4)]
    (union
     (translate [0 0, (/ height -2)]
                (binding [*fn* trackball-fn] (cylinder (- ball_place_radius 0) height)))
     (color CYA (translate [18 6, -55] (cube 28 31 50)))
     ;end union
     )))
