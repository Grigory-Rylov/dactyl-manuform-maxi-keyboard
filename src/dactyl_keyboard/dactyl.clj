(ns dactyl-keyboard.dactyl
  (:refer-clojure :exclude [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.hotswap :refer :all]
            [dactyl-keyboard.config :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.case :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.screws :refer :all]
            [dactyl-keyboard.plate :refer :all]))

(def larger-plate
  (let [plate-height (- (/ (- sa-double-length mount-height) 3) 0.5)
        top-plate (->> (cube mount-width plate-height web-thickness)
                       (translate [0 (/ (+ plate-height mount-height) 2)
                                   (- plate-thickness (/ web-thickness 2))]))]
    (union top-plate (mirror [0 1 0] top-plate))))

(def thumbcaps
  (union
   (thumb-layout (sa-cap 1))
   )
  )


(defn shape-insert [column row offset shape]
  (let [shift-right   (= column lastcol)
        shift-left    (= column 0)
        shift-up      (and (not (or shift-right shift-left)) (= row 0))
        shift-down    (and (not (or shift-right shift-left)) (>= row lastrow))
        position      (if shift-up     (key-position column row (map + (wall-locate2  0  1) [0 (/ mount-height 2) 0]))
                        (if shift-down  (key-position column row (map - (wall-locate2  0 -1) [0 (/ mount-height 2) 0]))
                          (if shift-left (map + (left-key-position row 0) (wall-locate3 -1 0))
                            (key-position column row (map + (wall-locate2  1  0) [(/ mount-width 2) 0 0])))))]
    (->> shape (translate (map + offset [(first position) (second position) 0])))))

(defn magnet-shape-insert [column row offset shape]
  (let [shift-right   (= column lastcol)
        shift-left    (= column 0)
        shift-up      (and (not (or shift-right shift-left)) (= row 0))
        shift-down    (and (not (or shift-right shift-left)) (>= row lastrow))
        position      (if shift-up     (key-position column row (map + (wall-locate2  0  1) [0 (/ mount-height 2) 0]))
                        (if shift-down  (key-position column row (map - (wall-locate2  0 -1) [0 (/ mount-height 2) 0]))
                          (if shift-left (map + (left-key-position row 0) (wall-locate3 -1 0))
                            (key-position column row (map + (wall-locate2  1  0) [(/ mount-width 2) 0 0])))))]

    (->> shape (translate (map + offset [(first position) (second position) (+ (/ magnet-diameter 2) magnet-wall-width)])))))

;;;;;;;;;;;;;;;;;;;;;
;; CONTOLLER HOLES ;;
;;;;;;;;;;;;;;;;;;;;;
; Offsets for the controller/trrs holder cutout
(def holder-offset
  (case nrows
    0 0
    1 0
    2 0
    3 0
    4 -2.5
    5 0
    6 (if inner-column
        3.2
        2.2)
    )
  )

(def notch-offset
  (case nrows
    0 0
    1 0
    2 0
    3 0
    4 3.35
    5 0.15
    6 -5.07))

; Cutout for controller/trrs jack holder https://github.com/rianadon/dactyl-configurator/blob/main/src/connectors.md
(defn usb-holder-hole [width step height]
  (union
   ;(translate [0, (* +1 step), 0] (cube width, step, height))
   (translate [0, (* -1 step), 0] (cube (- width (* step 2)), (* step  4), height ))
   (translate [0, step, 0] (cube width, step, height))
   (translate [0, (* step 2), 0] (cube width, (* step 2), height))
   (translate [0, (* -1 step), 0] (cube width, step, height))
   ))

(def left-offset
  (if niceNanoMode -16 -8)
  )
(def controller-holder-y-offset -0.4)
(def usb-holder-hole-space
  (shape-insert 1, 0, [left-offset controller-holder-y-offset (/ external-controller-height 2)]
                (usb-holder-hole external-controller-width external-controller-step external-controller-height)
                )
  )



(def pinky-walls
  (union
   (key-wall-brace lastcol cornerrow 0 -1 web-post-br lastcol cornerrow 0 -1 wide-post-br)
   (key-wall-brace lastcol 0 0 1 web-post-tr lastcol 0 0 1 wide-post-tr))
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; wrist rest magnetic holder mounting ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn magnet-hole  [radius inner-radius height]
  (rotate [0 (deg2rad 90) (deg2rad 90)]
          (union
           (binding [*fn* 100] (cylinder radius height))
           (binding [*fn* 100] (cylinder inner-radius (+ height 10))
             )
           )
          )
  )

(defn magnet-stiffness-booster  [side height]
  (rotate [0 (deg2rad 90) (deg2rad 90)]
          (cube side side height)
          )
  )
; keyboard's magnet hole
(def magnet-place
  (union
    ;(magnet-shape-insert 4, 2, [-10 -14.5 0] (magnet-hole (+ (/ magnet-diameter 2) 0.1) (/ magnet-inner-diameter 2) magnet-height))
    (magnet-shape-insert 3, 3, [0 -0.7 0] (magnet-hole (+ (/ magnet-diameter 2) 0.1) (/ magnet-inner-diameter 2) magnet-height))
   )
)

(def magnet-stiffness-booster
  (union
    (magnet-shape-insert 4, 2, [-10 (+ -14.5 wall-thickness) 0]
      (magnet-stiffness-booster (+ magnet-diameter 2) magnet-booster-width)
    )
    (magnet-shape-insert 3, 3, [0 (+ -0.7 wall-thickness) 0]
      (magnet-stiffness-booster (+ magnet-diameter 2) magnet-booster-width)
    )
  )
)

; wrist rest magnet holder
(def magnet-connector
  (difference
   (rotate [0 (deg2rad 90) (deg2rad 90)]
           (translate [0 0 (/ magnet-connector-length -2)]
     (binding [*fn* 100] (cylinder (+ (/ magnet-diameter 2) magnet-connector-wall-width) magnet-connector-length))
           )
           )
     (magnet-hole (+ (/ magnet-diameter 2) 0.1) (/ magnet-inner-diameter 2) magnet-height)
   )
)

(def magnet-connector-offset 1)
(def magnet-connectors
  (union
   (magnet-shape-insert 4, 2, [0 (- -13 (+ magnet-connector-offset (/ magnet-height 2))) 0] magnet-connector)
   (magnet-shape-insert 3, 3, [0 (- 0.75 (+ magnet-connector-offset (/ magnet-height 2))) 0] magnet-connector)
   )
  )

(def key-holes-right
  (apply union
         (for [column columns
               row rows
               :when (or (.contains [2 3] column)
                         (not= row lastrow))]
           (->> single-plate-right
                (key-place column row)))))

(def key-holes-left
  (apply union
         (for [column columns
               row rows
               :when (or (.contains [2 3] column)
                         (not= row lastrow))]
           (->> single-plate-left
                (key-place column row)))))

(def model-right (difference
                  (union
                   key-holes-right
                   pinky-connectors
                   pinky-walls
                   connectors
                   thumb-right
                   thumb-connectors
                   (difference (union case-walls
                                      (if magnet-holes magnet-stiffness-booster)
                                      screw-insert-outers)
                               usb-holder-hole-space
                               screw-insert-holes
                               (if magnet-holes magnet-place)
                               ))
                  (translate [0 0 -20] (cube 350 350 40))))

(def model-left-tmp (difference
                  (union
                   key-holes-left
                   pinky-connectors
                   pinky-walls
                   connectors
                   thumb-left
                   thumb-connectors
                   (difference (union case-walls
                                      (if magnet-holes magnet-stiffness-booster)
                                      screw-insert-outers)
                               usb-holder-hole-space
                               screw-insert-holes
                               (if magnet-holes magnet-place)
                               ))
                  (translate [0 0 -20] (cube 350 350 40))))

(spit "things/right.scad"
      (write-scad model-right))

(spit "things/left.scad"
      (write-scad (mirror [-1 0 0] model-left-tmp)))

(def caps
  (apply union
         (for [column columns
               row rows
               :when (or (.contains [2 3] column)
                         (not= row lastrow))]
           (->> (sa-cap (if (and (true? pinky-15u) (= column lastcol)) 1.5 1))
                (key-place column row)))))

(spit "things/right-test.scad"
      (write-scad
       (difference
        (union
         key-holes-right
         pinky-connectors
         pinky-walls
         connectors
         thumb-right
         thumb-connectors
         (if magnet-holes magnet-connectors)
         case-walls
         thumbcaps
         caps)

        (translate [0 0 -20] (cube 350 350 40)))))

(if magnet-holes
  (spit "things/right-wrist-connector.scad"
      (write-scad
       (difference
        (union
         magnet-connectors
        )
        )
       )
        )

  (spit "things/left-wrist-connector.scad"
        (write-scad
         (mirror [-1 0 0]
                 (difference
          (union
           magnet-connectors
           )
                  )
                 )
         )
        )

)
;;;;;;;;;;;;;;;;;;;;;
;; plate generation;;
;;;;;;;;;;;;;;;;;;;;;


(spit "things/right-plate-print.scad" (write-scad plate-right))
(spit "things/left-plate-print.scad" (write-scad (mirror [-1 0 0] plate-right)))

(spit "things/hotswap-adapt-low-debug.scad" (write-scad hot-socket-standart-to-low-profile))
(spit "things/hotswap-low-debug.scad" (write-scad hot-socket-low-profile))
(spit "things/hotswap-standart-debug.scad" (write-scad hot-socket-standart))

(defn -main [dum] 1)  ; dummy to make it easier to batch
