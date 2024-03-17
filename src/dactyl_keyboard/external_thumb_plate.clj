(ns dactyl-keyboard.external-thumb-plate
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.config :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.plate :refer :all]
            [dactyl-keyboard.0-thumbs-connectors :refer :all]
            [dactyl-keyboard.0-thumbs-case :refer :all]
            [dactyl-keyboard.keycaps :refer :all]
            [dactyl-keyboard.case :refer :all]
            [dactyl-keyboard.hotswap :refer :all]
            [dactyl-keyboard.screws :refer :all]
            [dactyl-keyboard.thumbs :refer :all]))

(def external-thumb-fill
  (external-4-thumbs-layout filled-plate))

(def external-thumb-outline
  (project
   (union
    external-4-thumbs-connectors
    external-thumb-fill
    external-thumb-right
    external-4-thumbs-case-walls)))

(def external-thumb-case
  (difference
   (translate [0, 0, plate-height] (mirror [-1, 0, 0] (mirror [0 0 -1] plate-right)))
   (extrude-linear
     {:height 20 :twist 0 :convexity 0} external-thumb-outline)))
