(ns dactyl-keyboard.thumbs
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.config :refer :all]))

;;;;;;;;;;;;
;; Thumbs ;;
;;;;;;;;;;;;

(def thumborigin
  (map + (key-position 1 cornerrow [(/ mount-width 2) (- (/ mount-height 2)) 0])
       thumb-offsets))

(def thumborigin-mod
  (map + (key-position 1 cornerrow [(/ mount-width 2) (- (/ mount-height 2)) 0])
       thumb-offsets-mod))

(defn thumb-tr-place [shape]
  (->> shape
       (rotate (deg2rad 14) [1 0 0])
       (rotate (deg2rad -15) [0 1 0])
       (rotate (deg2rad 15) [0 0 1]) ; original 10
       (translate thumborigin)
       (translate [-15 -10 5])))

; original 1.5u  (translate [-12 -16 3])
(defn thumb-tl-place [shape]
  (->> shape
       (rotate (deg2rad 10) [1 0 0])
       (rotate (deg2rad -23) [0 1 0])
       (rotate (deg2rad 25) [0 0 1]) ; original 10
       (translate thumborigin)
       (translate [-35 -16 -2])))

; original 1.5u (translate [-32 -15 -2])))

(defn thumb-6-place [shape]
  (->> shape
       (rotate (deg2rad 13) [1 0 0])
       (rotate (deg2rad -15) [0 1 0])
       (rotate (deg2rad 15) [0 0 1])
       (translate thumborigin)
       (translate [-6 -28 1])))

(defn thumb-mr-place [shape]
  (->> shape
       (rotate (deg2rad 10) [1 0 0])
       (rotate (deg2rad -23) [0 1 0])
       (rotate (deg2rad 25) [0 0 1])
       (translate thumborigin)
       (translate [-24 -35 -6])))
(defn thumb-br-place [shape]
  (->> shape
       (rotate (deg2rad 6) [1 0 0])
       (rotate (deg2rad -34) [0 1 0])
       (rotate (deg2rad 35) [0 0 1])
       (translate thumborigin)
       (translate [-39 -43 -16])))
(defn thumb-bl-place [shape]
  (->> shape
       (rotate (deg2rad 6) [1 0 0])
       (rotate (deg2rad -32) [0 1 0])
       (rotate (deg2rad 35) [0 0 1])
       (translate thumborigin)
       (translate [-51 -25 -11.5])))

;        (translate [-51 -25 -12])))
(def thumb-offset-x 28)
(def thumb-offset-y 13)

(defn thumb-top-place [shape]
  (->> shape
       (rotate (deg2rad 0) [1 0 0])
       (rotate (deg2rad 0) [0 1 0])
       (rotate (deg2rad 0) [0 0 1]) ; original 10
       (translate thumborigin)
       (translate [thumb-offset-x thumb-offset-y -3])))

(defn thumb-bottom-place [shape]
  (->> shape
       (rotate (deg2rad 0) [1 0 0])
       (rotate (deg2rad 0) [0 1 0])
       (rotate (deg2rad 0) [0 0 1])
       (translate thumborigin)
       (translate [(+ thumb-offset-x 0) (+ thumb-offset-y -42) -10])))

(defn thumb-middle-place [shape]
  (->> shape
       (rotate (deg2rad 0) [1 0 0])
       (rotate (deg2rad 0) [0 1 0])
       (rotate (deg2rad 0) [0 0 1])
       (translate thumborigin)
       (translate [(+ thumb-offset-x 0) (+ thumb-offset-y -28) -6.5])))


(defn thumb-place [rot move shape]
  (->> shape
       (rotate (deg2rad (nth rot 0)) [1 0 0])
       (rotate (deg2rad (nth rot 1)) [0 1 0])
       (rotate (deg2rad (nth rot 2)) [0 0 1]) ; original 10
       (translate thumborigin)
       (translate move)))

(def thumb-x-offset 10)
; convexer
(defn thumb-r-place [shape]
  (rotate [0, 0, (deg2rad board-z-angle)] (thumb-place [14 -40 10] [(+ -15 thumb-x-offset) -10 5] shape)))

; right
(defn thumb-m-place [shape]
  (rotate [0, 0, (deg2rad board-z-angle)] (thumb-place [12 -39 22] [(+ -32 thumb-x-offset) -14 -7] shape)))

; middle
(defn thumb-l-place [shape]
  (rotate [0, 0, (deg2rad board-z-angle)] (thumb-place [8 -44 28] [(+ -45.0 thumb-x-offset) -20.5 -21] shape)))



(defn thumb-m-place-mod [shape]
  (rotate [0, 0, (deg2rad board-z-angle)] (thumb-place [12 -39 22] [(+ -32 thumb-x-offset) -14 -7] shape)))

(defn thumb-r-place-mod [shape]
  (rotate [0, 0, (deg2rad board-z-angle)] (thumb-place [14 -40 10] [(+ -15 thumb-x-offset) -10 5] shape)))

;bottom right
(defn thumb-mr-place-mod-remove [shape]
  (->> shape
       (rotate (deg2rad 10) [1 0 0])
       (rotate (deg2rad -23) [0 1 0])
       (rotate (deg2rad 25) [0 0 1])
       (translate thumborigin-mod)
       (translate [-24 -35 -6])))
;bottom left
(defn thumb-br-place-mod-remove [shape]
  (->> shape
       (rotate (deg2rad 6) [1 0 0])
       (rotate (deg2rad -34) [0 1 0])
       (rotate (deg2rad 35) [0 0 1])
       (translate thumborigin-mod)
       (translate [-39 -43 -16])))

(defn thumb-l-place-mod [shape]
  (rotate [0, 0, (deg2rad board-z-angle)] (thumb-place [8 -44 28] [(+ -45.0 thumb-x-offset) -20.5 -21] shape))
  )

; left

(defn three-thumbs-layout [shape]
  (union
   (thumb-r-place shape)
   (thumb-m-place shape)
   (thumb-l-place shape)))

(defn three-thumbs-layout-mod [shape]
  (union
   ;(thumb-mr-place-mod shape)
   ;(thumb-br-place-mod shape)
   (thumb-m-place-mod shape)
   (thumb-r-place-mod shape)
   (thumb-l-place-mod shape)
   ; end union
   ))

(defn five-thumbs-layout [shape]
  (union
   (thumb-mr-place shape)
   (thumb-br-place shape)
   (thumb-tl-place shape)
   (thumb-tr-place shape)
   (thumb-bl-place shape)))

(defn six-thumbs-layout [shape]
  (union
   (thumb-mr-place shape)
   (thumb-6-place shape)
   (thumb-br-place shape)
   (thumb-tl-place shape)
   (thumb-bl-place shape)))

(defn external-4-thumbs-layout [shape]
  (union
   (color-red (thumb-middle-place shape))
   (color-green (thumb-bottom-place shape))
   (color-blue (thumb-top-place shape))))

(defn thumb-layout [shape]
  (case thumbs-count
    0 (external-4-thumbs-layout shape)
    3 (three-thumbs-layout-mod shape)
    5 (five-thumbs-layout shape)
    6 (six-thumbs-layout shape)))
