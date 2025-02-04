(ns dactyl-keyboard.connectors
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.connectors-common :refer :all]
            [dactyl-keyboard.0-thumbs-connectors :refer :all]
            [dactyl-keyboard.3-thumbs-connectors :refer :all]
            [dactyl-keyboard.3-thumbs-connectors-mod :refer :all]
            [dactyl-keyboard.5-thumbs-connectors :refer :all]
            [dactyl-keyboard.5-thumbs-connectors-mod :refer :all]
            [dactyl-keyboard.6-thumbs-connectors :refer :all]
            [dactyl-keyboard.thumbs :refer :all]
            [dactyl-keyboard.config :refer :all]))

;;;;;;;;;;;;;;;;;;;;
;; Web Connectors ;;
;;;;;;;;;;;;;;;;;;;;

(def connectors
  (let [tl web-post-tl-c
        tr web-post-tr-c
        bl web-post-bl-c
        br web-post-br-c]
  (apply union
         (concat
          ;; Row connections
          (for [column (range 0 (dec ncols))
                row    (range 0 (if extra-middle-row lastrow nrows))]
            (triangle-hulls
             (key-place (inc column) row tl)
             (key-place column row tr)
             (key-place (inc column) row bl)
             (key-place column row br)))

          ;; Column connections
          (for [column columns
                row    (range 0 cornerrow)]
            (triangle-hulls
             (key-place column row bl)
             (key-place column row br)
             (key-place column (inc row) tl)
             (key-place column (inc row) tr)))

          ;; Diagonal connections
          (for [column (range 0 (dec ncols))
                row    (range 0 cornerrow)]
            (triangle-hulls
             (key-place column row br)
             (key-place column (inc row)tr)
             (key-place (inc column) row bl)
             (key-place (inc column) (inc row) tl))))))
  )

(def thumb-connectors-right
  (case thumbs-count
    0 external-4-thumbs-connectors
    3 three-thumbs-connectors-mod
    5 five-thumbs-connectors
    6 six-thumbs-connectors))

(def thumb-connectors-left
  (case thumbs-count
    0 external-4-thumbs-connectors
    3 five-thumbs-connectors-mod
    5 five-thumbs-connectors
    6 six-thumbs-connectors))

(def pinky-connectors
  (apply union
         (concat
          ;; Row connections
          (for [row (range 0 nrows)]
            (triangle-hulls
             (key-place lastcol row web-post-tr)
             (key-place lastcol row wide-post-tr)
             (key-place lastcol row web-post-br)
             (key-place lastcol row wide-post-br)))

          ;; Column connections
          (for [row (range 0 cornerrow)]
            (triangle-hulls
             (key-place lastcol row web-post-br)
             (key-place lastcol row wide-post-br)
             (key-place lastcol (inc row) web-post-tr)
             (key-place lastcol (inc row) wide-post-tr)))
          ;;
          )))
