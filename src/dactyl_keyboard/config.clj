(ns dactyl-keyboard.config
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]))


(defn deg2rad [degrees]
  (* (/ degrees 180) pi))

;;;;;;;;;;;;;;;;;;;;;;
;; Shape parameters ;;
;;;;;;;;;;;;;;;;;;;;;;

(def nrows 3)
(def ncols 6)
(def extra-middle-row false)

(def externalThumb false)

(def column-curvature (deg2rad 17))

(def row-curvature (deg2rad 7))

(def board-z-angle 0)

; 5                   ; curvature of the rows

(def centerrow
  (if externalThumb
    (dec nrows)
    (if extra-middle-row (- nrows 3) (- nrows 2))))

; controls front-back tilt
(def centercol 2)

; controls left-right tilt / tenting (higher number is more tenting)
(def tenting-angle (if externalThumb (deg2rad 0) (deg2rad 25)))

; or, change this for more precise tenting control
(def column-style :standart)

; options include :standard, :orthographic, and :fixed
; (def column-style :fixed)
(def pinky-15u false)
(def extra-row false)

; adds an extra bottom row to the outer columns
(def inner-column false)

; adds an extra inner column (two less rows than nrows)

;external case for controller and ports
(def niceNanoMode false)
(def controller-plate-height 1.5)
; external controller holder
(def external-controller false)
(def external-controller-height 14)
(def external-controller-width
  (if niceNanoMode 25 33))

(def reset-hole-enabled false)
; magnet holes for external wrist rest
(def magnet-holes true)
(def magnet-height 2)
(def magnet-booster-width 1)
(def magnet-diameter 10)
(def magnet-wall-width 1)
(def magnet-connector-wall-width 1.5)
(def magnet-connector-length 4)
(def magnet-inner-diameter 3)

; If you want hot swap sockets enable this
; hotswap types:
; 0 - no hot-swap
; 1 - gateron standart
; 2 - gateron low profile
(def mono-mode false)
(def hot-swap 0)
(def hot-swap-holders true)

(def low-profile false)
(def cols-angle true)
(def plate-height 2)
(def plate-border-height 0)
(def trackball-mode true)

(def thumbs-count 3)

(def plate-bumpers true)
(def plate-bumper-radius 5)

;;;;;;;;;;;;;;;;;;;;
;; Column offsets ;;
;;;;;;;;;;;;;;;;;;;;
(def first_column_y_offset 2)

(defn column-offset [column]
  (cond
    (= column 2) [0 2.82 -4.5]
    (= column 4) [0 -18 5.64]
    ; pinky finger1
    (= column 5) [0 -20 5.64]
    ; pinky finger2
    (= column 0) [0 -7.8 3]
    ; index finger1
    (= column 1) [0 -5.8 3]
    ; index finger2
    :else        [0 -2 0]))

(defn last-column-offset [row]
  (cond
    (= row 0)    [0 2.82 -4.5]
    (= row 1)    [0 -18 5.64]
    ; pinky finger1
    (= row 2)    [0 -20 5.64]
    ; pinky finger2
    (= row 3)    [0 -7.8 3]
    ; index finger1
    (= row 4)    [0 -5.8 3]
    ; index finger2
    :else        [0 -2 0]))

; ring finger

;;;;;;;;;;;;;;;;;;;;;;;
;; General variables ;;
;;;;;;;;;;;;;;;;;;;;;;;

(def lastrow (if extra-middle-row (dec nrows) nrows))
(def cornerrow (if extra-middle-row (dec lastrow) (dec nrows)))
(def lastcol (dec ncols))
(def firstcol 0)
(def firstrow 0)
(def extra-cornerrow (if extra-row lastrow cornerrow))
(def innercol-offset (if inner-column 1 0))

(def thumb-offsets [6 -3 7])
(def thumb-offsets-mod [14 -3 7])

; высота
(def keyboard-z-offset
  16 ;(if hot-swap 12 9)
  )

; controls overall height; original=9 with centercol=3; use 16 for centercol=2

(def extra-width 2.5)

; extra space between the base of keys; original= 2
(def extra-height 1.0)

; original= 0.5

(def wall-z-offset -5)

; original=-15 length of the first downward-sloping part of the wall (negative)
(def wall-xy-offset 10)

; offset in the x and/or y direction for the first downward-sloping part of the wall (negative)
(def wall-thickness 3)
(def wall-thickness0 1)

; wall thickness parameter; originally 5

;; Settings for column-style == :fixed
;; The defaults roughly match Maltron settings
;;   http://patentimages.storage.googleapis.com/EP0219944A2/imgf0002.png
;; Fixed-z overrides the z portion of the column ofsets above.
;; NOTE: THIS DOESN'T WORK QUITE LIKE I'D HOPED.
(def fixed-angles [(deg2rad 10) (deg2rad 10) 0 0 0 (deg2rad -15) (deg2rad -15)])
(def fixed-angles-y [(deg2rad 0) 0 0 0 0 0 0])
(def fixed-x [-41.5 -22.5 0 20.3 41.4 65.5 89.6])

; relative to the middle finger
(def fixed-z [12.1 8.3 0 5 10.7 14.5 17.5])
(def fixed-tenting (deg2rad 0))

; If you use Cherry MX or Gateron switches, this can be turned on.
; If you use other switches such as Kailh, you should set this as false
(def create-side-nubs? false)

; Holes inside keyswitch
(def use-top-nub false)

;;;;;;;;;;;;;;;;;;;;;;;
;; General variables ;;
;;;;;;;;;;;;;;;;;;;;;;;
(def lastcol (dec ncols))

;;;;;;;;;;;;;;;;;
;; Switch Hole ;;
;;;;;;;;;;;;;;;;;

(def keyswitch-height 14)

;; Was 14.1, then 14.25
(def keyswitch-width 14)

(def sa-profile-key-height 12.7)

(def plate-thickness
  (if low-profile
    3
    4))

; толщина верхних стенок

(def side-nub-thickness 4)
(def retention-tab-thickness 1.5)
(def retention-tab-hole-thickness (- plate-thickness retention-tab-thickness))
(def mount-width (+ keyswitch-width 3))
(def mount-height (+ keyswitch-height 3))

(def mono_body_offsetX 75)

; trackball
(def trackball-ball-diameter 43)
(def trackball-bearing-diameter 2)
(def trackball-mount-distance 27)
(def trackball-mount-diameter 6)
(def trackball-place-wall 2)

(def trackball-ball-radius (/ trackball-ball-diameter 2))
(def trackball-bearing-radius (/ trackball-bearing-diameter 2))
(def trackball-mount-radius (/ trackball-mount-diameter 2))
(def trackball-fn 100)
(def trackball-offset-x -69)
(def trackball-offset-y -35)
(def tracball-offset-z 56)

(def fn-value 60) ; 60 in release
(def wall-fn 20)
(def border-inner-offset-vert-top 1)
(def border-inner-offset-vert-bottom -1)
(def border-outer-offset-vert-top 1.1)
(def border-outer-offset-vert-bottom -1.1)
(def border-inner-offset-hor-right 2)
(def thumb-border-inner-offset-hor-right 1)
(def border-inner-offset-hor-left -2)
(def border-outer-offset-hor-right 2.1)
(def border-outer-offset-hor-left -2.1)
(def screw-nut-diameter 4.0)
