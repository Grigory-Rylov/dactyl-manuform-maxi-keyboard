(ns dactyl-keyboard.dactyl
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.hotswap :refer :all]
            [dactyl-keyboard.config :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.case :refer :all]
            [dactyl-keyboard.case-common :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.screws :refer :all]
            [dactyl-keyboard.magnet-holder :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.0-thumbs-connectors :refer :all]
            [dactyl-keyboard.connectors-common :refer :all]
            [dactyl-keyboard.external-controller :refer :all]
            [dactyl-keyboard.controller-holes :refer :all]
            [dactyl-keyboard.external-thumb-plate :refer :all]
            [dactyl-keyboard.plate :refer :all]))

(def larger-plate
  (let [plate-height (- (/ (- sa-double-length mount-height) 3) 0.5)
        top-plate    (->> (cube mount-width plate-height web-thickness)
                          (translate
                           [0
                            (/ (+ plate-height mount-height) 2)
                            (- plate-thickness (/ web-thickness 2))]))]
    (union top-plate (mirror [0 1 0] top-plate))))

(def thumbcaps
  (union
   (thumb-layout (sa-cap 1))))

(def external-thumbcaps
  (union
   (external-4-thumbs-layout (sa-cap 1))))

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
        2.2)))

(def notch-offset
  (case nrows
    0 0
    1 0
    2 0
    3 0
    4 3.35
    5 0.15
    6 -5.07))

(def pinky-walls
  (union
   (key-wall-brace lastcol cornerrow 0 -1 web-post-br lastcol cornerrow 0 -1 wide-post-br)
   (key-wall-brace lastcol 0 0 1 web-post-tr lastcol 0 0 1 wide-post-tr)))

(def key-holes-right
  (if extra-middle-row
    (apply union
           (for [column columns
                 row    rows
                 :when  (or (.contains [2 3] column)
                            (not= row lastrow))]
             (->> single-plate-right
                  (key-place column row))))

    (apply union
           (for [column columns
                 row    rows]
             (->> single-plate-right
                  (key-place column row))))))

(def key-holes-left
  (if extra-middle-row
    (apply union
           (for [column columns
                 row    rows
                 :when  (or (.contains [2 3] column)
                            (not= row lastrow))]
             (->> single-plate-left
                  (key-place column row))))

    (apply union
           (for [column columns
                 row    rows]
             (->> single-plate-left
                  (key-place column row))))))

(def controller-hole
  (if external-controller
    external-controller-holder-hole-space
    internal-controller-hole))

(def offsetX 90)

(def logo
  ; logo
  (translate [-56, -55.3, 80]
             (rotate [(deg2rad 90), 0, 0]
                     (color-yellow
                      (import
                       "osik_logo.stl")))))

(def screw-holders-mid
  (union
   (color-green (translate [-86 -2 5] (cube 6 4 6)))
   (color-green (translate [-86 -53 5] (cube 6 4 6)))))
(def model-right
  (difference
   (union
    key-holes-right
    ;pinky-connectors
    ;pinky-walls
    connectors
    logo
    screw-holders-mid
    ;(color-green controller-hole)
    (if (= externalThumb false) thumb-right)
    (if (= externalThumb false) thumb-connectors)
    (difference
     (union case-walls
            (if magnet-holes magnet-stiffness-booster)
            screw-insert-outers)
     controller-hole
     screw-insert-holes
     (if magnet-holes magnet-place)))
   (color-green (translate [(- -50 offsetX), 0, 0] (cube 100 200 200)))
   (translate [0 0 -20] (cube 350 350 40))))

(def model-left
  (mirror [1, 0, 0]
          (difference
           (union
            key-holes-right
            ;pinky-connectors
            ;pinky-walls
            connectors
            (if (= externalThumb false) thumb-right)
            (if (= externalThumb false) thumb-connectors)
            (difference
             (union case-walls
                    (if magnet-holes magnet-stiffness-booster)
                    screw-insert-outers)
             screw-insert-holes
             (if magnet-holes magnet-place)))
           (translate [0 0 -20] (cube 350 350 40)))))


(def model-mono
  (difference
   (union
    (translate [offsetX, 0, 0] model-right)

    ;(translate [(* offsetX -1), 0, 0]  model-left)
    ;union
    )
   (translate [0 0 -20] (cube 350 350 40))))

(def model-left-tmp
  (difference
   (union
    key-holes-left
    ;pinky-connectors
    ;pinky-walls
    connectors
    (if (> thumbs-count 0) thumb-left)
    (if (> thumbs-count 0) thumb-connectors)
    (difference
     (union case-walls
            (if magnet-holes magnet-stiffness-booster)
            screw-insert-outers)
     controller-hole
     screw-insert-holes
     (if magnet-holes magnet-place)))
   (translate [0 0 -20] (cube 350 350 40))))

(def external-thumb-model-right
  (difference
   (union
    external-thumb-case
    ;pinky-connectors
    ;pinky-walls
    thumb-right
    thumb-connectors
    (difference
     (union external-thumbs-case-walls
            ;thumbs-screw-insert-outers
            )

     ;thumbs-screw-insert-holes
     ))
   (translate [0 0 -20] (cube 350 350 40))))


(spit "things/right.scad"
      (write-scad model-right))


(spit "things/mono_3x5_3.scad"
      (write-scad model-mono))

(spit "things/left.scad"
      (write-scad (mirror [-1 0 0] model-left-tmp)))

(def caps
  (if extra-middle-row
    (apply union
           (for [column columns
                 row    rows
                 :when  (or (.contains [2 3] column)
                            (not= row lastrow))]
             (->> (sa-cap (if (and (true? pinky-15u) (= column lastcol)) 1.5 1))
                  (key-place column row))))


    (apply union
           (for [column columns
                 row    rows]
             (->> (sa-cap (if (and (true? pinky-15u) (= column lastcol)) 1.5 1))
                  (key-place column row))))
    ; end if
    )
  ; end caps
  )

(spit "things/right-test.scad"
      (write-scad
       (difference
        (union
         key-holes-right
         ;pinky-connectors
         ;pinky-walls
         connectors
         (if (> thumbs-count 0) thumb-right) ; TODO remove condition in test
         (if (> thumbs-count 0) thumb-connectors)
         (if magnet-holes magnet-connectors)

         (difference
          (union case-walls (binding [*fn* 50])
                 (if magnet-holes magnet-stiffness-booster)
                 screw-insert-outers)
          controller-hole
          screw-insert-holes
          (if magnet-holes magnet-place))

         thumbcaps
         caps
         controller-body
         usb-connector-body-place
         ; end union
         )
        controller-hole

        (translate [0 0 -20] (cube 350 350 40)))))

(spit "things/right-external-thumb-test.scad"
      (write-scad
       (difference
        (union
         key-holes-right
         ;pinky-connectors
         ;pinky-walls
         connectors
         (if (> thumbs-count 0) thumb-right) ; TODO remove condition in test
         external-4-thumbs-connectors
         case-walls
         external-thumbcaps
         caps)

        (translate [0 0 -20] (cube 350 350 40)))))

(if magnet-holes
  (spit "things/right-wrist-connector.scad"
        (write-scad
         (difference
          (union
           magnet-connectors))))

  (spit "things/left-wrist-connector.scad"
        (write-scad
         (mirror [-1 0 0]
                 (difference
                  (union
                   magnet-connectors))))))

;;;;;;;;;;;;;;;;;;;;;
;; plate generation;;
;;;;;;;;;;;;;;;;;;;;;


(spit "things/right-plate-print.scad" (write-scad plate-right))
(spit "things/left-plate-print.scad" (write-scad (mirror [-1 0 0] plate-right)))

(spit "things/hotswap-adapt-low-debug.scad"
      (write-scad hot-socket-standart-to-low-profile))
(spit "things/hotswap-low-debug.scad" (write-scad hot-socket-low-profile))
(spit "things/hotswap-standart-debug.scad" (write-scad hot-socket-standart))


(spit "things/right-external-controller.scad" (write-scad external-controller-case))

(if externalThumb
  (spit "things/right-external-thumb-case.scad" (write-scad external-thumb-model-right)))
(if externalThumb
  (spit "things/right-external-thumb.scad"
        (write-scad
         (difference
          (union
           ;external-thumb-case
           thumb-right
           thumb-connectors
           (difference
            (union external-thumbs-case-walls
                   ;thumbs-screw-insert-outers
                   )

            ;thumbs-screw-insert-holes
            ))
          (translate [0 0 -10] (cube 350 350 40))))))


(defn -main [dum] 1)

; dummy to make it easier to batch
