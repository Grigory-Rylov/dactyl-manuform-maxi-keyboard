(ns dactyl-keyboard.case
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.3-thumbs-case :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.config :refer :all]))

(def case-walls
  (case thumbs-count
    3 three-thumbs-case-walls
    5 three-thumbs-case-walls
    6 three-thumbs-case-walls))
