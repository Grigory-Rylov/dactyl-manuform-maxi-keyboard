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

(defn thumb-place [rot move shape]
  (->> shape
       (rotate (deg2rad (nth rot 0)) [1 0 0])
       (rotate (deg2rad (nth rot 1)) [0 1 0])
       (rotate (deg2rad (nth rot 2)) [0 0 1]) ; original 10
       (translate thumborigin)
       (translate move)))

; convexer
(defn thumb-r-place [shape] (thumb-place [14 -40 10] [-15 -10 5] shape))

; right
(defn thumb-m-place [shape] (thumb-place [10 -32 39] [-33 -19 -8] shape))

; middle
(defn thumb-l-place [shape] (thumb-place [6 -30 49] [-46.0 -33.5 -19] shape))

; left

(defn three-thumbs-layout [shape]
  (union
   (thumb-r-place shape)
   (thumb-m-place shape)
   (thumb-l-place shape)))

(defn five-thumbs-layout [shape]
  (union
   (thumb-r-place shape)
   (thumb-m-place shape)
   (thumb-l-place shape)))


(defn six-thumbs-layout [shape]
  (union
   (thumb-r-place shape)
   (thumb-m-place shape)
   (thumb-l-place shape)))

(defn thumb-layout [shape]
  (case thumbs-count
    3 (three-thumbs-layout shape)
    5 (five-thumbs-layout shape)
    6 (six-thumbs-layout shape)))
