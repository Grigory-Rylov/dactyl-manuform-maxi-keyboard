(ns dactyl-keyboard.config
  (:refer-clojure :exclude [use import])
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
(def ncols 5)

(def column-curvature (deg2rad 20))                         ; 15                        ; curvature of the columns
(def row-curvature (deg2rad 6))                             ; 5                   ; curvature of the rows

(def centerrow (- nrows 2))             ; controls front-back tilt
(def centercol 2)                       ; controls left-right tilt / tenting (higher number is more tenting)
(def tenting-angle (deg2rad 15))            ; or, change this for more precise tenting control
(def column-style
  (if (> nrows 5) :orthographic :standard))  ; options include :standard, :orthographic, and :fixed
; (def column-style :fixed)
(def pinky-15u false)
(def extra-row false)                   ; adds an extra bottom row to the outer columns
(def inner-column false)                ; adds an extra inner column (two less rows than nrows)

;external case for controller and ports
(def niceNanoMode false)
(def external-controller true)
(def external-controller-height 10)
(def external-controller-width
  (if niceNanoMode 25 33)
  )

; magnet holes for external wrist rest
(def magnet-holes false)
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
(def hot-swap 0)
(def low-profile false)
(def plate-height 2)
(def plate-border-height 1)

(def six-thumb-mode false)

;;;;;;;;;;;;;;;;;;;;
;; Column offsets ;;
;;;;;;;;;;;;;;;;;;;;
(def first_column_y_offset 2)
(defn column-offset [column] (cond
                               (= column 2) [0 2.82 -4.5]
                               (= column 4) [0 -18 5.64]   ; pinky finger1
                               (= column 5) [0 -20 5.64]   ; pinky finger2
                               (= column 0) [0 -7.8 3]      ; index finger1
                               (= column 1) [0 -5.8 3]      ; index finger2
                               :else [0 -2 0]))              ; ring finger

;;;;;;;;;;;;;;;;;;;;;;;
;; General variables ;;
;;;;;;;;;;;;;;;;;;;;;;;

(def lastrow  (dec nrows))
(def cornerrow lastrow)
(def lastcol (dec ncols))
(def extra-cornerrow (if extra-row lastrow cornerrow))
(def innercol-offset (if inner-column 1 0))

(def thumb-offsets [6 -3 7])

(def keyboard-z-offset
  10 ;(if hot-swap 12 9)
  )               ; controls overall height; original=9 with centercol=3; use 16 for centercol=2

(def extra-width 2.5)                   ; extra space between the base of keys; original= 2
(def extra-height 1.0)                  ; original= 0.5

(def wall-z-offset -5)                 ; original=-15 length of the first downward-sloping part of the wall (negative)
(def wall-xy-offset 5)                  ; offset in the x and/or y direction for the first downward-sloping part of the wall (negative)
(def wall-thickness 3)                  ; wall thickness parameter; originally 5

;; Settings for column-style == :fixed
;; The defaults roughly match Maltron settings
;;   http://patentimages.storage.googleapis.com/EP0219944A2/imgf0002.png
;; Fixed-z overrides the z portion of the column ofsets above.
;; NOTE: THIS DOESN'T WORK QUITE LIKE I'D HOPED.
(def fixed-angles [(deg2rad 10) (deg2rad 10) 0 0 0 (deg2rad -15) (deg2rad -15)])
(def fixed-x [-41.5 -22.5 0 20.3 41.4 65.5 89.6])  ; relative to the middle finger
(def fixed-z [12.1    8.3 0  5   10.7 14.5 17.5])
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

(def keyswitch-height 14.2) ;; Was 14.1, then 14.25
(def keyswitch-width 14.2)

(def sa-profile-key-height 12.7)

(def plate-thickness
  (if low-profile
    3
    4
    )) ; толщина верхних стенок

(def side-nub-thickness 4)
(def retention-tab-thickness 1.5)
(def retention-tab-hole-thickness (- plate-thickness retention-tab-thickness))
(def mount-width (+ keyswitch-width 3))
(def mount-height (+ keyswitch-height 3))
