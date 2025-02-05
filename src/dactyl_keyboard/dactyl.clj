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
            [dactyl-keyboard.controller-holder-internal :refer :all]
            [dactyl-keyboard.screws :refer :all]
            [dactyl-keyboard.magnet-holder :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.0-thumbs-connectors :refer :all]
            [dactyl-keyboard.connectors-common :refer :all]
            [dactyl-keyboard.external-controller :refer :all]
            [dactyl-keyboard.controller-holes :refer :all]
            [dactyl-keyboard.external-thumb-plate :refer :all]
            [dactyl-keyboard.trackball :refer :all]
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
   (thumb-layout-right (sa-cap 1))))

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

(def logo-right
  ; logo
  (if mono-mode
    ; mono logo
    (translate [-56, -55.3, 80]
               (rotate [(deg2rad 90), 0, 0]
                       (color-yellow
                        (import
                         "osik_logo.stl"))))
    ;split logo
    (translate [(+ -7 thumb-x-offset), -65.5, 80]
               (rotate [(deg2rad 90), 0, (deg2rad 9)]
                       (color-yellow
                        (import
                         "osik_logo.stl"))))))

(def logo-left
  ; logo
  (if mono-mode
    ; mono logo
    (translate [-60, -55.3, 80]
               (rotate [(deg2rad 90), 0, 0]
                       (color-yellow
                        (mirror [1, 0, 0]
                                (import
                                 "osik_logo.stl")))))
    ;split logo
    (translate [-12.5, -66.5, 80]
               (rotate [(deg2rad 90), 0, (deg2rad 9)]
                       (color-yellow
                        (mirror [1, 0, 0]
                                (import
                                 "osik_logo.stl")))))))


(def screw-holders-mid-left
  (union
   (color-green (translate [-65.6 -1.8 5] (cube 6 4 6)))
   (color-green (translate [-65.6 -53 5] (cube 6 4 6)))))

(def screw-holders-mid-right
  (union
   (color-yellow (translate [-52 4 5] (rotate [0, 0, (deg2rad 45)] (cube 6 4 6))))
   (color-green (translate [-65.5 -53 5] (cube 6 4 6)))))

(def screw-holders-right
  (union
   (color-green (translate [-55 -8 5] (cube 6 4 6)))
   (color-yellow (translate [-52 21 5] (cube 6 4 6)))))

(def screw-holders-left
  (union
   (color-green (translate [-55 -8 5] (cube 6 4 6)))
   (color-yellow (translate [-52 21 5] (cube 6 4 6)))))

(def case-holder
  (union
   (color-blue (translate [2 0 2] (cube 4 2 2)))
   (color-green (cube 8 2 2)))
  ;end union
  )

(def case-holders-left
  (let [front_y_offset -52.5
        back_y_offset  -2.1
        top_z_offset   40.35]
    (union
     (translate [-75 front_y_offset 5] (rotate [(deg2rad 90), 0, 0] case-holder))
     (translate [-75 front_y_offset 36] (rotate [(deg2rad 90), 0, 0] case-holder))

     (translate [-75 back_y_offset 15] (rotate [(deg2rad -90), 0, 0] case-holder))
     (translate [-75 back_y_offset 36] (rotate [(deg2rad -90), 0, 0] case-holder))

     (translate [-75 -35 top_z_offset] (rotate [(deg2rad 0), 0, 0] case-holder))
     (translate [-75 -20 top_z_offset] (rotate [(deg2rad 0), 0, 0] case-holder))

     ;end union
     )))

(def model-right
  (let [tracball-hole-cylinder-rad    20
        tracball-hole-cylinder-height 20]
    (difference
     (union
      key-holes-right
      ;pinky-connectors
      ;pinky-walls
      connectors
      logo-right
      (if mono-mode
        screw-holders-mid-right
        (if (= external-controller false) screw-holders-right))

      ;(color-green controller-hole)
      (if (= externalThumb false) thumb-right)
      (if (= externalThumb false) thumb-connectors-right)
      (if trackball-mode

        (translate [trackball-offset-x, trackball-offset-y, tracball-offset-z]
                   (union
                    (translate [0, 0, -18] trackball-case)
                    trackball-walls)
                   ; end translate tb
                   )
        ; end
        )

      (difference
       (union
        (difference case-walls-right
                    (if trackball-mode
                      (translate [trackball-offset-x, trackball-offset-y, (+ 10 tracball-offset-z)]
                                 (color-red trackball-hole))))
        (if magnet-holes magnet-stiffness-booster)
        screw-insert-outers-right)
       controller-hole
       screw-insert-holes-right

       (if magnet-holes magnet-place)))
     (if mono-mode
       (color-green (translate [(- -50 mono_body_offsetX), 0, 0] (cube 100 200 200))))
     (translate [0 0 -20] (cube 350 350 40)))))

(def model-keymatrix-right
  (difference
   (union
    key-holes-right
    connectors
    thumb-right
    thumb-connectors-right
    key-matrix-border-right)
   (color-yellow keymatrix-screw-insert-right)))

(def model-right-case
  (let [tracball-hole-cylinder-rad    20
        tracball-hole-cylinder-height 20]
    (difference
     (union
      logo-right
      screw-holders-right

      (if trackball-mode
        (translate [trackball-offset-x, trackball-offset-y, tracball-offset-z]
                   (union
                    (translate [0, 0, -18] trackball-case)
                    trackball-walls)
                   ; end translate tb
                   )
        ; end
        )

      (difference
       (union
        keymatrix-holders
        (difference case-walls-right
                    case-screw-holders-holes-right
                    (if trackball-mode
                      (translate [trackball-offset-x, trackball-offset-y, (+ 10 tracball-offset-z)]
                                 (color-red trackball-hole))))
        (if magnet-holes magnet-stiffness-booster)
        screw-insert-outers-right)
       controller-hole

       (if magnet-holes magnet-place)
       case-screw-nut-holes-right
       ; end difference
       ))
     (if mono-mode
       (color-green (translate [(- -50 mono_body_offsetX), 0, 0] (cube 100 200 200))))
     (translate [0 0 -20] (cube 350 350 40))
     screw-insert-holes-right
     ; end total diff
     )))

(def model-left
  (mirror [1, 0, 0]
          (union
           (difference
            (union
             key-holes-left
             ;pinky-connectors1
             ;pinky-walls
             connectors
             logo-left
             (if mono-mode
               screw-holders-mid-left
               (if (= external-controller false) screw-holders-left))

             (if (= externalThumb false) thumb-left)
             (if (= externalThumb false) thumb-connectors-right)

             (difference
              (union case-walls-right
                     (if magnet-holes magnet-stiffness-booster)
                     screw-insert-outers-left)
              screw-insert-holes-left
              controller-hole
              (if magnet-holes magnet-place)))

            ;side cut
            (if mono-mode
              (color-green (translate [(- -50 mono_body_offsetX), 0, 0] (cube 100 200 200))))

            (translate [0 0 -20] (cube 350 350 40))
            ;end difference
            )
           (if mono-mode case-holders-left)
           ;end union
           )))

(def model-left-case
  (mirror [1, 0, 0]
          (union
           (difference
            (union
             key-holes-left
             ;pinky-connectors1
             ;pinky-walls
             connectors
             logo-left
             (if mono-mode
               screw-holders-mid-left
               (if (= external-controller false) screw-holders-left))

             (if (= externalThumb false) thumb-left)
             (if (= externalThumb false) thumb-connectors-right)

             (difference
              (union case-walls-right
                     (if magnet-holes magnet-stiffness-booster)
                     screw-insert-outers-left)
              screw-insert-holes-left
              controller-hole
              (if magnet-holes magnet-place)))

            ;side cut
            (if mono-mode
              (color-green (translate [(- -50 mono_body_offsetX), 0, 0] (cube 100 200 200))))

            (translate [0 0 -20] (cube 350 350 40))
            ;end difference
            )
           (if mono-mode case-holders-left)
           ;end union
           )))

(def model-keymatrix-left
  (mirror [0, 0, 0]
          (difference
           (union
            key-holes-left
            connectors
            thumb-left
            thumb-connectors-left
            key-matrix-border-left)
           (color-yellow keymatrix-screw-insert-left))))

(def model-mono-right
  (difference
   (translate [mono_body_offsetX, 0, 0] model-right)
   (translate [0 0 -20] (cube 350 350 40))))

(def model-mono-left
  (difference
   (translate [(* mono_body_offsetX -1), 0, 0] model-left)
   (translate [0 0 -20] (cube 350 350 40))))

(def model-left-tmp
  (difference
   (union
    key-holes-left
    ;pinky-connectors
    ;pinky-walls
    connectors
    (if (> thumbs-count 0) thumb-left)
    (if (> thumbs-count 0) thumb-connectors-right)
    (difference
     (union case-walls-right
            (if magnet-holes magnet-stiffness-booster)
            screw-insert-outers-left)
     controller-hole
     screw-insert-holes-left
     (if magnet-holes magnet-place)))
   (translate [0 0 -20] (cube 350 350 40))))

(def external-thumb-model-right
  (difference
   (union
    external-thumb-case
    ;pinky-connectors
    ;pinky-walls
    thumb-right
    thumb-connectors-right
    (difference
     (union external-thumbs-case-walls
            ;thumbs-screw-insert-outers
            )

     ;thumbs-screw-insert-holes
     ))
   (translate [0 0 -20] (cube 350 350 40))))


;(spit "things/right.scad" (write-scad model-right))

(spit "things/right-case.scad" (write-scad model-right-case))
(spit "things/right-keymatrix.scad" (write-scad model-keymatrix-right))
(spit "things/right.scad"
      (write-scad
       (union
        model-right-case
        (color-green model-keymatrix-right))))

(spit "things/left-case.scad" (write-scad model-left-case))
(spit "things/left-keymatrix.scad" (write-scad model-keymatrix-left))


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
       (union
        (difference
         (union
          key-holes-right
          ;pinky-connectors
          ;pinky-walls
          connectors
          (if (> thumbs-count 0) thumb-right) ; TODO remove condition in test
          (if (> thumbs-count 0) thumb-connectors-right)
          (if magnet-holes magnet-connectors)

          (difference
           (union case-walls-right (binding [*fn* 50])
                  (if magnet-holes magnet-stiffness-booster)
                  screw-insert-outers-right)
           controller-hole
           screw-insert-holes-right
           (if magnet-holes magnet-place))

          thumbcaps
          caps
          controller-body
          usb-connector-body-place
          ; end union
          )
         controller-hole

         (translate [0 0 -20] (cube 350 350 40)))
        (translate [-25, -46.0, 0] (rotate [0, 0, (deg2rad -6)] internal-controller-plate-case)))))


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
(def mono-plate
  (union
   (translate [mono_body_offsetX, 0, 0] plate-right)
   (translate [(* -1 mono_body_offsetX), 0, 0] plate-left)))

(def mono-plate-mid-offset 110)
(def mono-plate-mid-offset-side 0.2)
(def mono-plate-mid-angle 22.5)

(def mono-plate-mid
  (difference
   mono-plate
   (rotate [0, 0, (deg2rad (* mono-plate-mid-angle -1))]
           (translate [(* mono-plate-mid-offset -1), -50, 0] (cube 120, 200, 10)))
   (rotate [0, 0, (deg2rad mono-plate-mid-angle)]
           (translate [mono-plate-mid-offset, -50, 0] (cube 120, 200, 10)))))

(def mono-plate-left
  (difference
   (translate [(* -1 mono_body_offsetX), 0, 0] plate-left)
   (rotate [0, 0, (deg2rad (* mono-plate-mid-angle -1))]
           (translate [(* mono-plate-mid-offset-side -1), -50, 0] (cube 100, 200, 10)))
   ;end diff
   ))

(def mono-plate-right
  (difference
   (translate [mono_body_offsetX, 0, 0] plate-right)
   (rotate [0, 0, (deg2rad mono-plate-mid-angle)]
           (translate [mono-plate-mid-offset-side, -50, 0] (cube 100, 200, 10)))
   ;end diff
   ))


;(spit "things/right-plate-print.scad" (write-scad plate-right))
;(spit "things/left-plate-print.scad" (write-scad (mirror [-1 0 0] plate-right)))
(if  mono-mode
  (spit "things/mono-plate-print.scad"
        (write-scad
         (union
          mono-plate-mid
          (color-yellow mono-plate-left)
          (color-blue mono-plate-right)))))
(if mono-mode
  (spit "things/mono-plate-print-left.scad" (write-scad mono-plate-left))
  (spit "things/left-plate-print.scad" (write-scad plate-left)))

(if mono-mode
  (spit "things/mono-plate-print-right.scad" (write-scad mono-plate-right))
  (spit "things/right-plate-print.scad" (write-scad plate-right)))

(if mono-mode
  (spit "things/mono-plate-mid-print.scad" (write-scad mono-plate-mid)))

(if mono-mode
  (spit "things/mono-plate.scad" (write-scad mono-plate))
  ;end if
  )


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
           thumb-connectors-right
           (difference
            (union external-thumbs-case-walls
                   ;thumbs-screw-insert-outers
                   )

            ;thumbs-screw-insert-holes
            ))
          (translate [0 0 -10] (cube 350 350 40))))))

(spit "things/controller_holder_internal.scad"
      (write-scad internal-controller-plate-case))

(spit "things/trackball_basket.scad"
      (write-scad trackball-case))

(spit "things/trackball_case.scad"
      (write-scad
       (difference
        (translate [trackball-offset-x, trackball-offset-y, (- tracball-offset-z 10)]
                   (union
                    (translate [0, 0, -18] trackball-case)
                    trackball-walls))
        (translate [0 0 -20] (cube 350 350 40)))
       ; end write
       ))

(defn -main [dum] 1)

; dummy to make it easier to batch
