(ns dactyl-keyboard.controller-holes
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.case-common :refer :all]
            [dactyl-keyboard.magnet-holder :refer :all]
            [dactyl-keyboard.connectors-common :refer :all]
            [dactyl-keyboard.external-controller :refer :all]
            [dactyl-keyboard.config :refer :all]))

(def controller-body
  (let [z-offset (+ controller-plate-height 1)]
    (shape-insert 0, 0,
                  [14, 12, z-offset]
                  (union
                   (translate
                    [controller-body-x-offset,
                     (- (/ niceNanocontrollerLen -2) wall-thickness),
                     (+ (/ niceNanocontrollerHeight 2) 0)]
                    (cube niceNanocontrollerWidth niceNanocontrollerLen niceNanocontrollerHeight))))))

(def usb-connector-body
  (let [z-offset          (+ controller-plate-height 1)
        cylinder-depth    3.04
        cylinder-diameter 3.5
        holes-lenght      16]
    (union
     (cube 20 5 6.7)
     (translate [(/ holes-lenght 2), cylinder-depth, 0]
                (rotate [(deg2rad 90), 0, 0]
                        (binding [*fn* 50] (cylinder (/ cylinder-diameter 2) cylinder-depth))))
     (translate [(/ holes-lenght -2), cylinder-depth, 0]
                (rotate [(deg2rad 90), 0, 0]
                        (binding [*fn* 50] (cylinder (/ cylinder-diameter 2) cylinder-depth))))

     ;end union
     )))

(def usb-connector-body-place
  (let [z-offset          (+ controller-plate-height -0.2)
        usb-z-offset      (+ (/ usbHeight 2) controllerBottomHeight controllerHeight)
        cylinder-depth    3.04
        cylinder-diameter 3.5
        holes-lenght      16]
    (shape-insert 1, 0,
                  [-1, -3.3, z-offset]
                  (translate [0, 0, usb-z-offset]
                             (rotate [0, 0, (deg2rad column-1-z-angle)]
                                     (cube 20 5 9))))
    ;end union
    ))

(def internal-controller-hole
  (let [z-offset     (+ controller-plate-height -0.2)
        usb-z-offset (+ (/ usbHeight 2) controllerBottomHeight controllerHeight)]
    (union
     (shape-insert 1, 0,
                   [-1, -1, z-offset]
                   (rotate [0, 0, (deg2rad column-1-z-angle)]

                           (union
                            (translate [0, -5.0, usb-z-offset] usb-connector-body)
                            ; cable hole
                            (translate
                             [0, 3, usb-z-offset]
                             usbCableHole)

                            ; usb connector hole
                            (translate
                             [0, 0, usb-z-offset]
                             usbHole))))
     (shape-insert 0, 0,
                   [18, 12, (/ 13.5 2)]
                   (rotate [0, 0, (deg2rad column-0-z-angle)]
                           roundSwitcherHole))
     ; end union
     )))
