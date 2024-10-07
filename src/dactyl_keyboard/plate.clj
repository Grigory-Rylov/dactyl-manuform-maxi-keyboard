(ns dactyl-keyboard.plate
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.config :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.case :refer :all]
            [dactyl-keyboard.hotswap :refer :all]
            [dactyl-keyboard.screws :refer :all]
            [dactyl-keyboard.thumbs :refer :all]))

;;;;;;;;;;;;;;;;;;;;;
;; plate generation;;
;;;;;;;;;;;;;;;;;;;;;
(def nub-size
  (if use-top-nub 5 0))


(defn key-places [shape]
  (apply union
         (for [column columns
               row    rows]
           (->> shape
                (key-place column row)))))


(def filled-plate
  (->> (cube mount-height mount-width plate-thickness)
       (translate [0 0 (/ plate-thickness 2)])))

(def key-fills
  (key-places filled-plate))

(def thumb-fill
  (if (> thumbs-count 0) (thumb-layout filled-plate) filled-plate))


(def single-plate-right
  (let [top-wall     (->> (cube (+ keyswitch-width 3) 1.5 plate-thickness)
                          (translate
                           [0
                            (+ (/ 1.5 2) (/ keyswitch-height 2))
                            (/ plate-thickness 2)]))
        left-wall    (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                          (translate
                           [(+ (/ 1.5 2) (/ keyswitch-width 2))
                            0
                            (/ plate-thickness 2)]))
        side-nub     (->> (binding [*fn* 30] (cylinder 1 2.75))
                          (rotate (/ π 2) [1 0 0])
                          (translate [(+ (/ keyswitch-width 2)) 0 1])
                          (hull
                           (->> (cube 1.5 2.75 side-nub-thickness)
                                (translate
                                 [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                  0
                                  (/ side-nub-thickness 2)])))
                          (translate [0 0 (- plate-thickness side-nub-thickness)]))
        plate-half   (union top-wall left-wall (if create-side-nubs? (with-fn 100 side-nub)))
        top-nub      (->> (cube nub-size nub-size retention-tab-hole-thickness)
                          (translate [(+ (/ keyswitch-width 2)) 0 (/ retention-tab-hole-thickness 2)]))
        top-nub-pair (union top-nub
                            (->> top-nub
                                 (mirror [1 0 0])
                                 (mirror [0 1 0])))]
    (difference
     (union plate-half
            (->> plate-half
                 (mirror [1 0 0])
                 (mirror [0 1 0]))
            (if (> hot-swap 0) (mirror [0 0 0] hot-socket)))
     ; keyswitch holder hole
     (color-yellow (translate [0, 0, (+ 0.7 (- 2.8 1.3))] (cube 5, 15.0, 1)))

     (->>
      top-nub-pair
      (rotate (/ π 2) [0 0 1])))))

(def single-plate-left
  (let [top-wall     (->> (cube (+ keyswitch-width 3) 1.5 plate-thickness)
                          (translate
                           [0
                            (+ (/ 1.5 2) (/ keyswitch-height 2))
                            (/ plate-thickness 2)]))
        left-wall    (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                          (translate
                           [(+ (/ 1.5 2) (/ keyswitch-width 2))
                            0
                            (/ plate-thickness 2)]))
        side-nub     (->> (binding [*fn* 30] (cylinder 1 2.75))
                          (rotate (/ π 2) [1 0 0])
                          (translate [(+ (/ keyswitch-width 2)) 0 1])
                          (hull
                           (->> (cube 1.5 2.75 side-nub-thickness)
                                (translate
                                 [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                  0
                                  (/ side-nub-thickness 2)])))
                          (translate [0 0 (- plate-thickness side-nub-thickness)]))
        plate-half   (union top-wall left-wall (if create-side-nubs? (with-fn 100 side-nub)))
        top-nub      (->> (cube nub-size nub-size retention-tab-hole-thickness)
                          (translate [(+ (/ keyswitch-width 2)) 0 (/ retention-tab-hole-thickness 2)]))
        top-nub-pair (union top-nub
                            (->> top-nub
                                 (mirror [1 0 0])
                                 (mirror [0 1 0])))]
    (difference
     (union plate-half
            (->> plate-half
                 (mirror [1 0 0])
                 (mirror [0 1 0]))
            (if (> hot-swap 0) (mirror [1 0 0] hot-socket)))
     ; keyswitch holder hole
     (color-yellow (translate [0, 0, (+ 0.7 (- 2.8 1.3))] (cube 5, 15.0, 1)))

     (->>
      top-nub-pair
      (rotate (/ π 2) [0 0 1])))))


(def thumb-right
  (union
   (thumb-layout single-plate-right)))

(def thumb-left
  (union
   (thumb-layout single-plate-left)))

(def external-thumb-right
  (union
   (external-4-thumbs-layout single-plate-right)))

(def model-outline
  (project
   (union
    key-fills
    connectors
    thumb-fill
    (if (= externalThumb false) thumb-right)
    (if (= externalThumb false) thumb-connectors)
    case-walls)))

(def case-walls-outline
  (cut case-walls))

(def wall-shape-right
  (cut
   (translate [0 0 -0.1]
              (union case-walls
                     screw-insert-outers-right))))

(def wall-shape-left
  (cut
   (translate [0 0 -0.1]
              (union case-walls
                     screw-insert-outers-left))))


(def bottom-height-half (/ plate-height 2))

(def bottom-plate-screw-holder-outline-right
  (cut screw-insert-outers-for-plate-right))

(def bottom-plate-screw-holder-outline-left
  (cut screw-insert-outers-for-plate-left))

(def bottom-plate-right
  (union
   ; body projection
   (translate [0 0 bottom-height-half]
              (extrude-linear {:height plate-height :twist 0 :convexity 0} model-outline))

   ; trackball case
   (if trackball-mode
     (translate [trackball-offset-x, trackball-offset-y, bottom-height-half]
                (binding [*fn* trackball-fn]
                  (cylinder
                    (+ trackball-ball-radius trackball-bearing-radius trackball-place-wall), plate-height))))

   ; borders
   (if (> plate-border-height 0)
     (translate [0 0 (+ plate-height (/ plate-border-height 2))]
                (extrude-linear {:height plate-border-height :twist 0 :convexity 0} case-walls-outline)))

   ; screw borders
   (if (> plate-border-height 0)
     (color-red
      (translate [0 0 (+ plate-height (/ plate-border-height 2))]
                 (extrude-linear {:height plate-border-height :twist 0 :convexity 0} bottom-plate-screw-holder-outline-right))))

   ; end union
   ))

(def bottom-plate-left
  (union
   ; body projection
   (translate [0 0 bottom-height-half]
              (extrude-linear {:height plate-height :twist 0 :convexity 0} model-outline))
   ; borders
   (if (> plate-border-height 0)
     (translate [0 0 (+ plate-height (/ plate-border-height 2))]
                (extrude-linear {:height plate-border-height :twist 0 :convexity 0} case-walls-outline)))

   ; screw borders
   (if (> plate-border-height 0)
     (color-red
      (translate [0 0 (+ plate-height (/ plate-border-height 2))]
                 (extrude-linear {:height plate-border-height :twist 0 :convexity 0} bottom-plate-screw-holder-outline-left))))

   ; end union
   ))

(def plate-bumpers-left
  (let [height 1]
    (union
     ;back center
     (translate [6, 35, (/ height 2)] (binding [*fn* 20] (cylinder plate-bumper-radius height)))
     ;back left
     (translate [78, 3, (/ height 2)] (binding [*fn* 20] (cylinder plate-bumper-radius height)))

     ;back right
     (translate [-50, 5, (/ height 2)] (binding [*fn* 20] (cylinder plate-bumper-radius height)))


     ;front left
     (translate [68, -64, (/ height 2)] (binding [*fn* 20] (cylinder plate-bumper-radius height)))

     ;front right
     (translate [-50, -70, (/ height 2)] (binding [*fn* 20] (cylinder plate-bumper-radius height)))

     ; end union

     )
    ))


(def plate-bumpers-right
  (let [height 1]
    (union
     plate-bumpers-left

     ;trackball
     (translate [-80, -20, (/ height 2)] (binding [*fn* 20] (cylinder plate-bumper-radius height)))

     ; end union

     )
    ))
(def plate-right
  (difference
   bottom-plate-right
   screw-insert-screw-holes-for-plate-right
   (if mono-mode
     (color-green (translate [(- -50 mono_body_offsetX), 0, 0] (cube 100 200 200))))
   (if plate-bumpers plate-bumpers-right)
   ; end difference
   ))

(def plate-left
  (mirror [1, 0, 0]
          (difference
           bottom-plate-left
           screw-insert-screw-holes-for-plate-left
           (if mono-mode
             (color-green (translate [(- -50 mono_body_offsetX), 0, 0] (cube 100 200 200))))
           (if plate-bumpers plate-bumpers-left)
           ; end difference
           )))

