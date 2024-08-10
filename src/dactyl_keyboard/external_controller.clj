(ns dactyl-keyboard.external-controller
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
            [dactyl-keyboard.config :refer :all]))

(def bracingLen 1.9)
(def bracingStep 1.5)
(def bracingWidth (* bracingStep 2))
(def controllerWallWidth 1)

(def niceNanocontrollerWidth 18)
(def niceNanocontrollerLen 35)
(def niceNanocontrollerHeight 1.7)

; purple
(def rpPurpleControllerWidth 21)
(def rpPurpleControllerLen 55)
(def rpPurpleControllerHeight 1.7)

; rp2040 black
(def rpBlackControllerWidth 23)
(def rpBlackControllerLen 54.3)
(def rpBlackControllerHeight 1.5)

(def usbHoleTopOffset 0)

(def controllerWidth
  (if niceNanoMode niceNanocontrollerWidth rpPurpleControllerWidth))
(def controllerLen (if niceNanoMode niceNanocontrollerLen rpPurpleControllerLen))
(def controllerHeight
  (if niceNanoMode niceNanocontrollerHeight rpBlackControllerHeight))

(def controllerBottomHeight 1.0)
(def roundCornerHeight 1)
(def roundCornerRadius 1)
(def totalControllerBoxHeight 6)
(def totalControllerBoxLen (+ controllerLen roundCornerRadius roundCornerRadius))
(def totalControllerBoxWidth (+ controllerWidth roundCornerRadius roundCornerRadius))
(def controllerWiringHoleWidth 4)
(def left-offset
  (if niceNanoMode -16 -11.3))
(def controllerCaseWidth (- external-controller-width 0.5))

;usb
(def usbWidth 9)
(def usbHeight 3.3)
(def usbCornerRadius 1.2)

;usb cable
(def usbCableWidth 13.5)
(def usbCableHeigth 11)
(def usbCableCornerRadius 3)

;trrs
(def trrsDiameter 6.2)

; real size 5.8
(def trrsLen1 4)
(def trrsLen2 12)
(def trrsContactsLen 6.5)
(def trrsOutterDiameter 8)

(def batteryLen 67.5)
(def batteryDiameter 18.1)
(def batteryBoxHeight (+ (/ batteryDiameter 2) controllerBottomHeight))
(def isExternalResetButtonEnabled true)

(def controller-holder-y-offset -0.4)

; Cutout for controller/trrs jack holder https://github.com/rianadon/dactyl-configurator/blob/main/src/connectors.md
(defn usb-holder-hole [width step height]
  (let [right-y-offset 3
        bracing-width  (* step 2)]
    (union
     ;(translate [0, (* +1 step), 0] (cube width, step, height))
     ; left

     ;right

     ;middle
     (translate [0, (* -1 step), 0] (cube (- width (* step 2)), (* step 4), height))
     (translate [0, step, 0] (cube width, step, height))
     (translate [0, (* step 2), 0] (cube width, (* step 2), height))
     (translate [0, (* -1 step), 0] (cube width, step, height))
     ; union
     )))

(def external-controller-holder-hole-space
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
   (cube controllerWidth, controllerLen, (- totalControllerBoxHeight roundCornerHeight))
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

(def usbHole
  (minkowski
   (cube 9, 4.7, 10)
   (rotate (binding [*fn* 50] (cylinder (/ trrsDiameter 2), 10)))))

(def bracingLeft
  (difference
   (cube bracingWidth, (* bracingStep 3), external-controller-height)
   (translate [(/ bracingStep 2), 0, 0]
              (cube bracingStep, bracingLen, external-controller-height))))

(def bracingLeftHole
  (translate [(/ bracingStep 2), 0, 0]
             (cube bracingStep, bracingLen, external-controller-height)))

(def bracingRight
  (translate
   [0, 0, 0] ; [(- controllerWallWidth (* bracingWidth 2)), (* controllerWallWidth -1), 0]

   (difference
    (cube bracingWidth, (* bracingStep 3), external-controller-height)

    (translate [(/ bracingStep -2), (- bracingStep 0.0), 0]
               (cube bracingStep, (+ bracingStep 0.1), external-controller-height))

    (translate [(/ bracingStep -2), (* (- bracingStep 0.0) -1), 0]
               (cube bracingStep, (+ bracingStep 0.1), external-controller-height)))))

(def controller-body-y-offset (/ totalControllerBoxLen -2))
(def controller-body-x-offset
  (/ (- controllerCaseWidth totalControllerBoxWidth) 2))

(def controller-body-x-offset-wireless
  (- (/ (- controllerCaseWidth totalControllerBoxWidth) 2) 2.7))

(def trrsHole
  (rotate [(deg2rad 90), 0, 0]
          (union
           (binding [*fn* 50] (cylinder (/ trrsDiameter 2), 10))
           (translate [0, 0, (+ wall-thickness (/ trrsLen2 2) -1.5)]
                      (binding [*fn* 50] (cylinder (/ trrsOutterDiameter 2), trrsLen2))))))

(def roundSwitcherHole
  (rotate [(deg2rad 90), 0, 0]
          (binding [*fn* 50] (cylinder (/ 6 2), 10))))


(def usbHole
  (minkowski
   (rotate [(deg2rad 90), 0, 0] (binding [*fn* 30] (cylinder usbCornerRadius, 10)))
   (cube (- usbWidth (* usbCornerRadius 2)), (+ wall-thickness first_column_y_offset), (- usbHeight (* usbCornerRadius 2)))))


(def usbCableHole
  (minkowski
   (rotate [(deg2rad 90), 0, 0] (binding [*fn* 30] (cylinder usbCableCornerRadius, 1)))
   (cube (- usbCableWidth (* usbCableCornerRadius 2)), (+ wall-thickness first_column_y_offset), (- usbCableHeigth (* usbCableCornerRadius 2)))))
(def holesFaceOffset -0.5)

(def controllerHolderCylinderDiameter 1.5)
(def controllerHolderOffset 6)
(def controllerHolderHelper
  (color-red
   (translate
    [0,
     0,
     (+ controllerHeight controllerBottomHeight (/ controllerHolderCylinderDiameter 2))]
    (translate [0, controllerHolderOffset, 0]
               (rotate [(deg2rad 90), 0, 0]
                       (binding [*fn* 30] (cylinder (/ controllerHolderCylinderDiameter 2), 3))))
    (translate [0, (- controllerLen controllerHolderOffset), 0]
               (rotate [(deg2rad 90), 0, 0]
                       (binding [*fn* 30] (cylinder (/ controllerHolderCylinderDiameter 2), 3))))
    (translate [controllerWidth, controllerHolderOffset, 0]
               (rotate [(deg2rad 90), 0, 0]
                       (binding [*fn* 30] (cylinder (/ controllerHolderCylinderDiameter 2), 3))))
    (translate [controllerWidth, (- controllerLen controllerHolderOffset), 0]
               (rotate [(deg2rad 90), 0, 0]
                       (binding [*fn* 30] (cylinder (/ controllerHolderCylinderDiameter 2), 3)))))))

(def external-controller-case-wired
  (union
   (difference
    (union

     (shape-insert 1, 0,
                   [(+ left-offset controller-body-x-offset)
                    controller-body-y-offset
                    (/ totalControllerBoxHeight 2)]

                   controller-holder-body)

     (intersection
      (shape-insert 1, 0, [left-offset controller-holder-y-offset (/ external-controller-height 2)]
                    (color-blue
                     (cube (- controllerCaseWidth (* bracingWidth 2)), (+ first_column_y_offset wall-thickness), external-controller-height)))
      front-walls)


     (shape-insert 1, 0,
                   [(+ (/ (- controllerCaseWidth bracingWidth) 2) left-offset),
                    -0.15,
                    (/ external-controller-height 2)]
                   (color-green bracingLeft))

     (shape-insert 1, 0,
                   [(+ (/ (- controllerCaseWidth bracingWidth) -2) left-offset),
                    (- (* first_column_y_offset -1) 0.12),
                    (/ external-controller-height 2)]
                   (color-blue bracingRight)))

    ; holes started

    (shape-insert 1, 0,
                  [left-offset, 0, 0]
                  (union
                   ; usb hole
                   (translate
                    [controller-body-x-offset,
                     0,
                     (+ (/ usbHeight 2) controllerBottomHeight controllerHeight)]
                    usbHole)

                   ;usb cable hole
                   (translate
                    [controller-body-x-offset,
                     3,
                     (+ (/ usbHeight 2) controllerBottomHeight controllerHeight)]
                    usbCableHole)

                   ; trrs hole
                   (translate [(+ (/ controllerCaseWidth -2) 6.1), 0, 5] trrsHole)

                   ; wiring hole
                   (translate
                    [controller-body-x-offset,
                     (- (/ controllerLen -2) controllerWallWidth),
                     (- controllerBottomHeight 0.5)]

                    ; Right wiring hole
                    (translate
                     [(- (+ controllerWallWidth roundCornerRadius) (/ controllerWidth 2)),
                      holesFaceOffset,
                      0]
                     (cube controllerWiringHoleWidth, (- controllerLen roundCornerRadius), (+ controllerBottomHeight 1)))

                    ; Left wiring hole
                    (translate
                     [(- (/ controllerWidth 2) controllerWallWidth roundCornerRadius),
                      holesFaceOffset,
                      0]
                     (cube controllerWiringHoleWidth, (- controllerLen roundCornerRadius), (+ controllerBottomHeight 1))))

                   ; controller hole
                   (translate
                    [controller-body-x-offset,
                     controller-body-y-offset,
                     (/ external-controller-height 2)]
                    (translate [0, 0, controllerBottomHeight]
                               (cube controllerWidth, (- controllerLen controllerYOffset), external-controller-height)))))

    (shape-insert 1, 0,
                  [(+ (/ (- controllerCaseWidth bracingWidth) 2) left-offset),
                   -0.15,
                   (/ external-controller-height 2)]
                  bracingLeftHole)
    ; hole end
    )

   (shape-insert 1, 0,
                 [(+ left-offset controller-body-x-offset (/ controllerWidth -2))
                  (+ controller-body-y-offset (/ controllerLen -2))
                  0]
                 controllerHolderHelper)

   ;union end
   ))

(def contactsWallWidth 2)
(def contactsWidth 1.08)
(def batteryCoverDiameter (+ batteryDiameter controllerWallWidth 2))

(def batteryContactsHolder
  (let [width  4
        height batteryBoxHeight
        depth  contactsWallWidth]
    (translate [(/ (- batteryDiameter width) -2), 0, 0]
               (union
                (difference
                 (cube width depth height)
                 (translate [0, (/ contactsWidth 2), 0] (cube width contactsWidth height)))

                (translate [(- batteryDiameter width), 0, 0]
                           (difference
                            (cube width, depth, height)
                            (translate [0, (/ contactsWidth 2), 0] (cube width, contactsWidth, height))))))))

(def outerContactsCover
  (let [contactWidth 6
        width        (+ contactWidth 2)
        height       batteryBoxHeight
        depth        2
        holeDepth    1]
    (difference
     (cube width depth height)
     (translate [0, (/ (- depth holeDepth) 2), 0] (cube contactWidth holeDepth height)))))

(def battery-box
  (let [totalBatteryBoxLen   (+ batteryLen (* (+ contactsWallWidth contactsWidth roundCornerRadius) 2))
        batteryCoverDiameter (+ batteryDiameter controllerWallWidth 2)
        topHoleLen           (- batteryLen 35)
        isDebug              false
        holesFaceOffset      (+ contactsWallWidth contactsWidth)
        centerHoleWidth      8
        batteryBoxWidth      (+ batteryDiameter (+ batteryLen (* (+ contactsWallWidth contactsWidth) 2)))
        batteryBoxHoleLen    (+ batteryLen (* (+ contactsWallWidth contactsWidth) 2))
        roundCornerRadius    1.7]

    (translate [7, (+ (/ totalBatteryBoxLen -2) roundCornerRadius 0.1), 2]
               (rotate [0, 0, (deg2rad 8)]
                       (union
                        ; top
                        (translate
                         [0,
                          0,
                          (/ batteryDiameter 4)]
                         (difference
                          (rotate [(deg2rad -90), 0, 0]
                                  (binding [*fn* 60] (cylinder (/ batteryCoverDiameter 2), batteryLen)))

                          (rotate [(deg2rad -90), 0, 0]
                                  (binding [*fn* 60] (cylinder (+ (/ batteryDiameter 2) 0.2), (+ batteryLen 1))))

                          (translate
                           [0,
                            0,
                            (/ batteryCoverDiameter -4)]
                           (cube batteryCoverDiameter, batteryLen, (/ batteryCoverDiameter 2)))

                          ; top middle hole
                          (translate [0, 7, (/ batteryCoverDiameter 4)]
                                     (cube batteryCoverDiameter, topHoleLen, (/ batteryCoverDiameter 2)))

                          ; top edge hole
                          (translate [0, (/ batteryLen -2), (/ batteryCoverDiameter 4)]
                                     (cube batteryCoverDiameter, 30, (/ batteryCoverDiameter 2)))

                          ; end difference
                          ))

                        ;battery
                        (if isDebug
                          (translate
                           [0,
                            (+ roundCornerRadius contactsWallWidth contactsWidth),
                            (+ (/ batteryDiameter 2) controllerBottomHeight)]
                           (color-red
                            (rotate [(deg2rad -90), 0, 0] (cylinder (/ batteryDiameter 2), batteryLen)))))

                        ; box
                        ; contacts 1
                        (translate [0, (/ (- batteryBoxHoleLen contactsWallWidth) 2), 0] batteryContactsHolder)

                        ; contacts 2
                        (translate
                         [0,
                          (/ (- batteryBoxHoleLen contactsWallWidth) -2)
                          0]
                         (mirror [0, 1, 0] batteryContactsHolder))

                        (difference
                         ;battery box
                         (union

                          (minkowski
                           (cube batteryDiameter (+ batteryLen (* (+ contactsWallWidth contactsWidth) 2))
                                 (- batteryBoxHeight roundCornerHeight))
                           (binding [*fn* 60] (cylinder roundCornerHeight roundCornerRadius))
                           ; end minkowski
                           )
                          ; end union
                          )

                         ; battery hole
                         (translate [0, 0, controllerBottomHeight]
                                    (cube batteryDiameter (+ batteryLen (* (+ contactsWallWidth contactsWidth) 2)) batteryBoxHeight))
                         ; wide hole
                         (translate [0, 0, (/ (- batteryBoxHeight roundCornerHeight) -2)]
                                    (cube batteryDiameter batteryLen (+ controllerBottomHeight 1)))

                         ; center hole
                         (translate [0, 0, (/ (- batteryBoxHeight roundCornerHeight) -2)]
                                    (cube centerHoleWidth (+ batteryLen (* holesFaceOffset 2)) (+ controllerBottomHeight 1)))
                         ; end union
                         )

                        (color-red
                         (translate
                          [0,
                           (/ (+ totalBatteryBoxLen 2) -2),
                           0]
                          outerContactsCover))

                        ; end union
                        )
                       ;end rotate
                       ))))

(def isDebug false)
(def batteryBoxXOffset -3)
(def batteryBoxYOffset (- (/ batteryLen -2) controllerLen))

(def buttonBottomOffset 0.5)
(def buttonHoleLeftOffset (+ (/ controllerWidth 2) roundCornerRadius))
(def buttonDiameter 3)
(def buttonClickDiameter 1.6)
(def buttonHeight 5.2)
(def buttonWidth 6)
(def buttonDepth 1.5)

(def switchHole
  ;switcher button hole
  (union
   (rotate [(deg2rad -90), 0, 0]
           (union
            ; click hole
            (translate [0, 0, (/ 10 2)] (binding [*fn* 50] (cylinder (/ buttonClickDiameter 2) 10)))
            (translate [0, 0, (/ (+ buttonDepth) 2)]
                       (binding [*fn* 50] (cylinder (/ buttonDiameter 2) (/ controllerWallWidth 2))))
            ;union
            ))
   ;switcher case hole
   (translate
    [0, 0, 0.5]
    (cube buttonWidth buttonDepth (+ buttonHeight 0.5)))
   ; union
   ))


(def external-case-body
  (let [topOffset   (/ external-controller-height -2)
        holderWidth (+ buttonWidth 1)]

    (union

     ; controller body
     (translate
      [controller-body-x-offset-wireless
       controller-body-y-offset
       (+ totalControllerBoxHeight topOffset)]
      controller-holder-body)

     ; bracing left
     (translate
      [(/ (- controllerCaseWidth bracingWidth) 2),
       -0.15,
       (/ totalControllerBoxHeight 2)]
      (color-green bracingLeft))

     ; bracing right
     (translate
      [(/ (- controllerCaseWidth bracingWidth) -2),
       (- (* first_column_y_offset -1) 0.12),
       (/ totalControllerBoxHeight 2)]
      (color-blue bracingRight))
     ; end union
     )))

(def external-case-holes
  (let [zOffset -2]
    (union
     ; button
     (translate
      [controller-body-x-offset-wireless,
       (- (/ buttonDepth -2) controllerWallWidth),
       (+ (/ usbHeight 2) controllerBottomHeight buttonHeight zOffset -0.5)]
      switchHole)
     ; usb hole
     (translate
      [controller-body-x-offset-wireless,
       0,
       (+ (/ usbHeight 2) controllerBottomHeight zOffset)]
      usbHole)

     ;usb cable hole
     (translate
      [controller-body-x-offset-wireless,
       3.5,
       (+ (/ usbHeight 2) controllerBottomHeight zOffset)]
      usbCableHole)

     ; wiring hole
     (translate
      [controller-body-x-offset-wireless,
       (- (/ controllerLen -2) controllerWallWidth),
       (- controllerBottomHeight 0.5)]

      ; Right wiring hole
      (translate
       [(- (+ controllerWallWidth roundCornerRadius) (/ controllerWidth 2)),
        holesFaceOffset,
        0]
       (cube controllerWiringHoleWidth, (- controllerLen roundCornerRadius 1), (+ controllerBottomHeight 10)))

      ; Left wiring hole
      (translate
       [(- (/ controllerWidth 2) controllerWallWidth roundCornerRadius),
        holesFaceOffset,
        0]
       (cube controllerWiringHoleWidth, (- controllerLen roundCornerRadius 1), (+ controllerBottomHeight 10)))
      ;switchHole hole
      ;
      )

     ; controller hole
     (translate
      [controller-body-x-offset-wireless,
       controller-body-y-offset,
       (+ (/ (+ totalControllerBoxHeight 2) 2) zOffset 1)]

      (translate [0, 0, controllerBottomHeight]
                 (cube controllerWidth, (- controllerLen controllerYOffset), (+ totalControllerBoxHeight 4))))
     ; end union
     )))

(def external-case-body-wireless
  (union
   ; wall
   (intersection
    (shape-insert 1, 0, [left-offset controller-holder-y-offset (/ totalControllerBoxHeight 2)]
                  (color-blue
                   (cube (- controllerCaseWidth (* bracingWidth 2)), (+ first_column_y_offset wall-thickness), external-controller-height)))
    front-walls)

   ; body

   (shape-insert 1, 0,
                 [left-offset, 0, 0]
                 external-case-body)

   (shape-insert 1, 0,
                 [(+ batteryBoxXOffset left-offset),
                  (- 0 totalControllerBoxLen controller-holder-y-offset (* controllerWallWidth 2)),
                  0]
                 ; battery box
                 battery-box)))


(def external-controller-case-wireless
  (let [topOffset   (/ external-controller-height -2)
        holderWidth (+ buttonWidth 1)
        zOffset     -2]
    (union
     (difference
      external-case-body-wireless
      (shape-insert 1, 0, [left-offset, 0, 0] external-case-holes)
      ; end difference
      )

     ;switch case
     (shape-insert 1, 0, [left-offset, 0, 0]
                   (difference
                    (translate
                     [(- controller-body-x-offset-wireless 1),
                      (- (/ buttonDepth -2) controllerWallWidth),
                      (+ (/ usbHeight 2) controllerBottomHeight buttonHeight zOffset -0.8)]
                     (color-yellow
                      (cube holderWidth, (+ buttonDepth 2), (+ 4.5 controllerWallWidth))))


                    ; button
                    (translate
                     [controller-body-x-offset-wireless,
                      (- (/ buttonDepth -2) controllerWallWidth),
                      (+ (/ usbHeight 2) controllerBottomHeight buttonHeight zOffset -0.5)]
                     switchHole)))

     (shape-insert 1, 0,
                   [(+ left-offset controller-body-x-offset-wireless (/ controllerWidth -2))
                    (+ controller-body-y-offset (/ controllerLen -2))
                    zOffset]
                   controllerHolderHelper)
     ;end union
     )))

(def external-controller-case-wireless1
  (union
   (difference
    (union

     (shape-insert 1, 0,
                   [(+ left-offset controller-body-x-offset-wireless)
                    controller-body-y-offset
                    (/ totalControllerBoxHeight 2)]

                   controller-holder-body)

     (intersection
      (shape-insert 1, 0, [left-offset controller-holder-y-offset (/ totalControllerBoxHeight 2)]
                    (color-blue
                     (cube (- controllerCaseWidth (* bracingWidth 2)), (+ first_column_y_offset wall-thickness), external-controller-height)))
      front-walls)


     (shape-insert 1, 0,
                   [(+ (/ (- controllerCaseWidth bracingWidth) 2) left-offset),
                    -0.15,
                    (/ totalControllerBoxHeight 2)]
                   (color-green bracingLeft))

     (shape-insert 1, 0,
                   [(+ (/ (- controllerCaseWidth bracingWidth) -2) left-offset),
                    (- (* first_column_y_offset -1) 0.12),
                    (/ totalControllerBoxHeight 2)]
                   (color-blue bracingRight)))

    ; holes started

    (shape-insert 1, 0,
                  [left-offset, 0, 0]
                  (union
                   ; usb hole
                   (translate
                    [controller-body-x-offset,
                     0,
                     (+ (/ usbHeight 2) controllerBottomHeight controllerHeight)]
                    usbHole)

                   ;usb cable hole
                   (translate
                    [controller-body-x-offset,
                     3,
                     (+ (/ usbHeight 2) controllerBottomHeight controllerHeight)]
                    usbCableHole)

                   ; wiring hole
                   (translate
                    [controller-body-x-offset,
                     (- (/ controllerLen -2) controllerWallWidth),
                     (- controllerBottomHeight 0.5)]

                    ; Right wiring hole
                    (translate
                     [(- (+ controllerWallWidth roundCornerRadius) (/ controllerWidth 2)),
                      holesFaceOffset,
                      0]
                     (cube controllerWiringHoleWidth, (- controllerLen roundCornerRadius), (+ controllerBottomHeight 1)))

                    ; Left wiring hole
                    (translate
                     [(- (/ controllerWidth 2) controllerWallWidth roundCornerRadius),
                      holesFaceOffset,
                      0]
                     (cube controllerWiringHoleWidth, (- controllerLen roundCornerRadius), (+ controllerBottomHeight 1))))

                   ; controller hole
                   (translate
                    [controller-body-x-offset,
                     controller-body-y-offset,
                     (/ totalControllerBoxHeight 2)]
                    (translate [0, 0, controllerBottomHeight]
                               (cube controllerWidth, (- controllerLen controllerYOffset), totalControllerBoxHeight)))))
    ; hole end
    )

   (shape-insert 1, 0,
                 [(+ left-offset controller-body-x-offset-wireless (/ controllerWidth -2))
                  (+ controller-body-y-offset (/ controllerLen -2))
                  0]
                 controllerHolderHelper)
   (shape-insert 1, 0,
                 [(+ left-offset controller-body-x-offset-wireless) ; x
                  0
                  ; y
                  0
                  ;( + roundCornerHeight usbHeight controllerBottomHeight 2)
                  ]
                 ; z
                 (color-yellow
                  (translate [0, 0, 0] switchHole)))

   (shape-insert 1, 0,
                 [(+ (/ (- controllerCaseWidth bracingWidth) -2) left-offset),
                  (- (* first_column_y_offset -1) 0.12),
                  (/ totalControllerBoxHeight 2)]
                 ; battery
                 (if isDebug
                   (translate
                    [(/ batteryDiameter 2),
                     (+ roundCornerRadius contactsWallWidth contactsWidth batteryBoxYOffset),
                     (+ (/ batteryDiameter 2) controllerBottomHeight)]
                    (color-red
                     (rotate [(deg2rad -90), 0, 0] (cylinder (/ batteryDiameter 2) batteryLen))))))

   ; battery box
   ;(translate [batteryBoxXOffset, 0, 0] battery-box)

   ; battery debug


   ;union end
   ))

(def external-controller-case
  (if niceNanoMode
    external-controller-case-wireless
    external-controller-case-wired))
