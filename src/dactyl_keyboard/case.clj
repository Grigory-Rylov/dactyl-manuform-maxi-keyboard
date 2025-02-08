(ns dactyl-keyboard.case
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.0-thumbs-case :refer :all]
            [dactyl-keyboard.3-thumbs-case :refer :all]
            [dactyl-keyboard.3-thumbs-case-mod :refer :all]
            [dactyl-keyboard.5-thumbs-case :refer :all]
            [dactyl-keyboard.5-thumbs-case-mod :refer :all]
            [dactyl-keyboard.6-thumbs-case :refer :all]
            [dactyl-keyboard.connectors :refer :all]
            [dactyl-keyboard.config :refer :all]))

(def case-walls-right
  (cond
    (= externalThumb true)  no-thumb-case-walls
    (= thumbs-count 3)      (union
                             (difference three-thumb-case-walls-mod three-thumb-case-matrix-border-right)
                             ;(color  YEL three-thumb-case-matrix-border)
                             )
    (= thumbs-count 5)      fifth-thumb-case-walls
    :else                   six-thumb-case-walls))

(def case-walls-left
  (cond
    (= externalThumb true)  no-thumb-case-walls
    (= thumbs-count 3)      (union
                             (difference fifth-thumb-case-walls-mod five-thumb-case-matrix-border-left-mod)
                             ;(color  YEL three-thumb-case-matrix-border)
                             )
    (= thumbs-count 5)      fifth-thumb-case-walls-mod
    :else                   six-thumb-case-walls))

(def external-thumbs-case-walls
  (case thumbs-count
    0 external-4-thumbs-case-walls
    3 external-4-thumbs-case-walls
    5 external-4-thumbs-case-walls
    6 external-4-thumbs-case-walls))

(def key-matrix-border-right
  three-thumb-case-matrix-border-right
  )

(def key-matrix-border-left
  five-thumb-case-matrix-border-left-mod
  )
