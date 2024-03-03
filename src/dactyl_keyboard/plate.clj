(ns dactyl-keyboard.plate
  (:refer-clojure :exclude [use import])
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
  (if use-top-nub 5 0)
  )


(defn key-places [shape]
  (apply union
         (for [column columns row rows]
           (->> shape
                (key-place column row)))))


(def filled-plate
  (->> (cube mount-height mount-width plate-thickness)
       (translate [0 0 (/ plate-thickness 2)])
       ))

(def key-fills
  (key-places filled-plate))


(def thumb-fill (thumb-layout filled-plate))

(def single-plate-right
  (let [top-wall (->> (cube (+ keyswitch-width 3) 1.5 plate-thickness)
                      (translate [0
                                  (+ (/ 1.5 2) (/ keyswitch-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                       (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                   0
                                   (/ plate-thickness 2)]))
        side-nub (->> (binding [*fn* 30] (cylinder 1 2.75))
                      (rotate (/ π 2) [1 0 0])
                      (translate [(+ (/ keyswitch-width 2)) 0 1])
                      (hull (->> (cube 1.5 2.75 side-nub-thickness)
                                 (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                             0
                                             (/ side-nub-thickness 2)])))
                      (translate [0 0 (- plate-thickness side-nub-thickness)]))
        plate-half (union top-wall left-wall (if create-side-nubs? (with-fn 100 side-nub)))
        top-nub (->> (cube nub-size nub-size  retention-tab-hole-thickness)
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
            (if (> hot-swap 0) (mirror [0 0 0] hot-socket))
            )
     (->>
      top-nub-pair
      (rotate (/ π 2) [0 0 1])))))

(def single-plate-left
  (let [top-wall (->> (cube (+ keyswitch-width 3) 1.5 plate-thickness)
                      (translate [0
                                  (+ (/ 1.5 2) (/ keyswitch-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                       (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                   0
                                   (/ plate-thickness 2)]))
        side-nub (->> (binding [*fn* 30] (cylinder 1 2.75))
                      (rotate (/ π 2) [1 0 0])
                      (translate [(+ (/ keyswitch-width 2)) 0 1])
                      (hull (->> (cube 1.5 2.75 side-nub-thickness)
                                 (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                             0
                                             (/ side-nub-thickness 2)])))
                      (translate [0 0 (- plate-thickness side-nub-thickness)]))
        plate-half (union top-wall left-wall (if create-side-nubs? (with-fn 100 side-nub)))
        top-nub (->> (cube nub-size nub-size retention-tab-hole-thickness)
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
            (if (> hot-swap 0) (mirror [1 0 0] hot-socket))
            )
     (->>
      top-nub-pair
      (rotate (/ π 2) [0 0 1])))))




(def thumb-right
  (union
   (thumb-layout single-plate-right)
   ))

(def thumb-left
  (union
   (thumb-layout single-plate-left)
   ))

(def model-outline
  (project
   (union
    key-fills
    connectors
    thumb-fill
    thumb-right
    thumb-connectors
    case-walls
    )
   )
  )
(def case-walls-outline
  (cut case-walls )
  )

(def wall-shape
  (cut
   (translate [0 0 -0.1]
              (union case-walls
                     screw-insert-outers
                     )
              ))
  )

(def bottom-height-half (/ plate-height 2))
(def bottom-plate
  (union
   (translate [0 0 bottom-height-half]
              (extrude-linear {:height plate-height :twist 0 :convexity 0} model-outline)
              )
   ( if (> plate-border-height 0)
     (translate [0 0 (+ plate-height (/ plate-border-height 2))]
                (extrude-linear {:height plate-border-height :twist 0 :convexity 0} case-walls-outline)
                )
     )
   )
  )
(def plate-right (difference
                  bottom-plate
                  screw-insert-screw-holes
                  ))

