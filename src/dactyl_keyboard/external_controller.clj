(ns dactyl-keyboard.external-controller
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.case :refer :all]
            [dactyl-keyboard.magnet-holder :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.config :refer :all]))

(def bracingLen 1.9)
(def bracingStep 1.5)
(def bracingWidth (* bracingStep 2))
(def controllerWallWidth 1)
(def controllerWidth 21)
(def controllerLen 55)
(def controllerHeight 1.7)
(def controllerBottomHeight 1)
(def roundCornerHeight 1)
(def roundCornerRadius 1)
(def totalControllerBoxLen (+ controllerLen roundCornerRadius roundCornerRadius))
(def totalControllerBoxWidth (+ controllerWidth roundCornerRadius roundCornerRadius))
(def left-offset
  (if niceNanoMode -16 -10))

(def controller-holder-y-offset -0.4)
; Cutout for controller/trrs jack holder https://github.com/rianadon/dactyl-configurator/blob/main/src/connectors.md
(defn usb-holder-hole [width step height]
  (union
   ;(translate [0, (* +1 step), 0] (cube width, step, height))
   (translate [0, (* -1 step), 0] (cube (- width (* step 2)), (* step 4), height))
   (translate [0, step, 0] (cube width, step, height))
   (translate [0, (* step 2), 0] (cube width, (* step 2), height))
   (translate [0, (* -1 step), 0] (cube width, step, height))))

(def usb-holder-hole-space
  (shape-insert 1, 0, [left-offset controller-holder-y-offset (/ external-controller-height 2)]
                (usb-holder-hole external-controller-width bracingStep external-controller-height)))

(def front-walls
  (union
   (color-blue (key-wall-brace 1 0 0 1 web-post-tl 0 0 0 1 web-post-tr))
   (color-red (key-wall-brace 1 0 0 1 web-post-tl 1 0 0 1 web-post-tr))
   (color-red (key-wall-brace 0 0 0 1 web-post-tl 0 0 0 1 web-post-tr))))

(def controllerYOffset 1)

(def controller-holder-body
  (minkowski
   (cube controllerWidth, controllerLen, (- external-controller-height roundCornerHeight))
   (binding [*fn* 50] (cylinder roundCornerHeight, roundCornerRadius))))

;(def controller-case-wired
;
;  (translate
;   [(/ (- external-controller-width totalControllerBoxWidth) 2),
;    (- (/ totalControllerBoxLen 2) controller-holder-y-offset first_column_y_offset),
;    0]
;   controller-holder))

;(def controller-case-wirless
;  (union
;   (color-blue
;    (cube external-controller-width, (+ first_column_y_offset wall-thickness), external-controller-height))
;   (translate
;    [(/ (- external-controller-width totalControllerBoxWidth) -2),
;     (- (/ (+ first_column_y_offset wall-thickness totalControllerBoxLen) 2) wall-thickness),
;     0]
;    controller-holder)))

;(def bracingRight)
(def bracingLeft
  (translate
   [0, 0, 0] ; [(- controllerWallWidth (* bracingWidth 2)), (* controllerWallWidth -1), 0]

   (difference
    (cube bracingWidth, (* bracingStep 3), external-controller-height)
    (translate [(/ bracingStep 2), (/ bracingStep 2), 0]
               (cube bracingStep, bracingLen, external-controller-height)))))

(def bracingRight
  (translate
   [0, 0, 0] ; [(- controllerWallWidth (* bracingWidth 2)), (* controllerWallWidth -1), 0]

   (difference
    (cube bracingWidth, (* bracingStep 3), external-controller-height)
    (translate [(/ bracingStep -2), (/ bracingStep 2), 0]
               (cube bracingStep, bracingLen, external-controller-height)))))

(def controller-body-y-offset (/ totalControllerBoxLen -2))
(def controller-body-x-offset
  (/ (- external-controller-width totalControllerBoxWidth) 2))


(def external-controller-case
  (difference
   (union
    (shape-insert 1, 0,
                  [(+ left-offset controller-body-x-offset)
                   controller-body-y-offset
                   (/ external-controller-height 2)]
                  controller-holder-body)

    (intersection
     (shape-insert 1, 0, [left-offset controller-holder-y-offset (/ external-controller-height 2)]
                   (color-blue
                    (cube (- external-controller-width (* bracingWidth 2)), (+ first_column_y_offset wall-thickness), external-controller-height)))
     front-walls)


    (shape-insert 1, 0, [
                          (+ (/ ( - external-controller-width bracingWidth) 2) left-offset),
                          -0.15,
                          (/ external-controller-height 2)]
                  (color-green bracingLeft))

    (shape-insert 1, 0, [
                          (+ (/ (- external-controller-width bracingWidth) -2) left-offset),
                          (- (* first_column_y_offset -1) 0.12),
                          (/ external-controller-height 2)]
                  (color-blue bracingRight))

    )

   (shape-insert 1, 0,
                 [(+ left-offset controller-body-x-offset)
                  controller-body-y-offset
                  (/ external-controller-height 2)]

                 (translate [0, 0, controllerBottomHeight]
                            (cube controllerWidth, (- controllerLen controllerYOffset), external-controller-height)))))

