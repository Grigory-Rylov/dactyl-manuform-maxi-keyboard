(ns dactyl-keyboard.hotswap
  (:refer-clojure :exclude
                  [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [dactyl-keyboard.common :refer :all]
            [dactyl-keyboard.config :refer :all]))

;;;;;;;;;;;;;;
;; Hot swap ;;
;;;;;;;;;;;;;;

(def socket-height-adjust 1.2)
(def socket-thickness 1.0)
(def socket-wall-height 3.8)
(def hot-swap-diameter 3.3)
(def hot-swap-vertical-offset -1)
(def hot-swap-radius (/ hot-swap-diameter 2))

(def hot-swap-hole-position-x 2.475)
(def hot-swap-hole-position-y 0.325)
(def hot-swap-hole-offset-y 2.20)
(def hot-swap-hole-offset-x 5)

;; low profile
(def hot-swap-low-profile-height 1.85)
(def hot-swap-low-profile-half-width 4.65)
(def hot-swap-low-profile-length 9.55)
(def hot-swap-low-profile-half-length 4.75)

(def gateron-hot-socket
  (union
   (translate [-4.2, 4.9, 0]
              (binding [*fn* 100] (cylinder 2.2, socket-wall-height)))
   (translate [-4.2, 4, 0]
              (cube 4, 2, socket-wall-height))
   (translate [1, 4.8, 0]
              (cube 9, 4.3, socket-wall-height))
   (difference
    (translate [-4.0, 2.8, 0]
               (cube 9, 4, socket-wall-height))
    (translate [0, 0.55, 0]
               (binding [*fn* 100] (cylinder 2.3 socket-wall-height))))
   (translate [7.4, 4.5, 0]
              (cube 4, 11, socket-wall-height))
   (translate [-9, 4.9, 0]
              (cube 3, 8, socket-wall-height))))

(def gateron-hot-socket2
  (union
   (translate [-4.2, 4.9, 0]
              (binding [*fn* 100] (cylinder 2.2, socket-wall-height)))
   (translate [-4.2, 4, 0]
              (cube 4, 2, socket-wall-height))
   (translate [1, 4.8, 0]
              (cube 9, 4.5, socket-wall-height))
   (difference
    (translate [-4.0, 2.8, 0]
               (cube 9, 4.5, socket-wall-height))
    (translate [0, 0.55, 0]
               (binding [*fn* 100] (cylinder 2.3 socket-wall-height))))
   (translate [7.4, 4.5, 0]
              (cube 4, 12, socket-wall-height))
   (translate [-9, 4.9, 0]
              (cube 3, 8, socket-wall-height))))

(defn gateronHotSocketShape [height]
  (def x1 6.55)
  (def y1 0)
  (def x2 (+ x1 1.28))
  (def y2 (+ y1 0.91))
  (def x3 (+ x2 4))
  (def y3 y2)
  (def x4 x3)
  (def y4 (- y3 4.42))
  (def x5 (- x4 3.43))
  (def y5 y4)
  (def x6 (- x5 2.33))
  (def y6 (- y5 1.18))
  (def x7 (- x6 6.1))
  (def y7 y6)
  (mirror [0, 1, 0]
          (extrude-linear {:height height :twist 0 :convexity 0}
                          (polygon
                           [[0, 0], [x1, y1], [x2, y2], [x3, y3], [x4, y4], [x5, y5], [x6, y6], [x7, y7]]))))

(def gateron-hot-socket-low-profile
  (union
   (translate [-5, 2.3, 0]
              (gateronHotSocketShape socket-wall-height))

   (translate [8.8, 5, 0]
              (cube 4, 6.5, socket-wall-height))

   (translate [-7.0, 5.4, 0]
              (cube 4, 6.0, socket-wall-height))))

; adapte standart hot socket to low profile switches
(def hot-socket-standart-to-low-profile
  (translate [0, 0, 1.04]
             (difference
              (union
               ; hot-swap plate
               ; hot-swap plate

               (translate [0 0 (- hot-swap-vertical-offset (/ socket-thickness 2))]
                          (cube (+ keyswitch-height 2.8) (+ keyswitch-width 3) socket-thickness))

               (translate [0, 0, -3.1]
                          (difference
                           (translate [0, 2.8, 0] (cube 17.8, 11.5, socket-wall-height))
                           (rotate [0, 0, (deg2rad -31)]
                                   (translate [0, 1.1, 0] gateron-hot-socket2)))))

              ; hot-swap socket hole
              ; keyboard center hole
              (translate [0, 0, -2.6] (binding [*fn* 50] (cylinder 2.5 3.2)))

              ; socket connector 1 hole
              (translate [4.4 4.7 (* (+ socket-height-adjust socket-thickness) -1)]
                         (binding [*fn* 50] (cylinder hot-swap-radius (+ socket-thickness 10))))

              ; socket connector 2 hole
              (translate [-2.6 5.75 (* (+ socket-height-adjust socket-thickness) -1)] ; -3.875 -2.215
                         (binding [*fn* 50] (cylinder hot-swap-radius (+ socket-thickness 10))))

              ;half hole
              (translate [0 (/ (+ keyswitch-width 3) -3) (- -1.05 socket-thickness)]
                         (cube (+ keyswitch-height 3.6) (/ (+ keyswitch-width 3) 3) 3.1))
              ;(binding [*fn* 50] (cylinder 2 2))
              )))

(def hot-socket-low-profile
  (translate [0, 0, 1.04]
             (difference
              (union
               ; hot-swap plate
               (translate [0 0 (- hot-swap-vertical-offset (/ socket-thickness 2))]
                          (cube (+ keyswitch-height 2.8) (+ keyswitch-width 3) socket-thickness))

               (translate [0, 0, -3.1]
                          (difference
                           (translate [0, 3.1, 0.1] (cube 17.0, 12, (+ socket-wall-height 0.2)))
                           (translate [0, 1.1, -0.8] gateron-hot-socket-low-profile))
                          ; socket holder barrier
                          (translate [0, 0, -1.70]
                                     (translate [-1.7, 3.3, 0]
                                                (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder 0.2 6))))
                                     (translate [-2, 8.1, 0]
                                                (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder 0.2 5.5))))

                                     (translate [4.6, 2.5, 0]
                                                (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder 0.2 4.7))))
                                     (translate [5.2, 7, 0]
                                                (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder 0.2 3.2)))))))
              ; hot-swap socket hole

              ; keyboard center hole
              (translate [0, 0, -2.6] (binding [*fn* 50] (cylinder 2.5 3.2)))

              ; socket connector 1 hole
              (translate [4.4 4.7 (* (+ socket-height-adjust socket-thickness) -1)]
                         (binding [*fn* 50] (cylinder hot-swap-radius (+ socket-thickness 10))))

              ; socket connector 2 hole
              (translate [-2.6 5.75 (* (+ socket-height-adjust socket-thickness) -1)] ; -3.875 -2.215
                         (binding [*fn* 50] (cylinder hot-swap-radius (+ socket-thickness 10))))

              ;half hole
              (translate [0 (/ (+ keyswitch-width 3) -3) (- -1.05 socket-thickness)]
                         (cube (+ keyswitch-height 3.6) (/ (+ keyswitch-width 3) 3) 3.1))

              ;corner cut

              (translate [10, 9, -2.5]
                         (rotate (deg2rad 45) [0, 0, 1] (cube 5, 5, 5)))
              (translate [-10, 9, -2.5]
                         (rotate (deg2rad 45) [0, 0, 1] (cube 5, 5, 5))))))

(def hot-socket-standart
  (translate [0, 0, 0.7]
             (difference
              (union
               ; hot-swap plate
               (difference
                (color-yellow
                 (translate [0 0 (- hot-swap-vertical-offset (/ socket-height-adjust 2))]
                            (cube (+ keyswitch-height 3.4) (+ keyswitch-width 3) (+ socket-thickness socket-height-adjust))))
                (translate [0 0 (- (/ socket-height-adjust -2) -0.5)]
                           (cube keyswitch-height keyswitch-width (+ socket-height-adjust 2.2))))


               (translate [0, 0, -4.1]
                          (difference
                           (translate [0, 2.8, 0] (cube 17.8, 11.5, socket-wall-height))
                           gateron-hot-socket)

                          ; socket holder
                          (if hot-swap-holders
                            (translate [0, 0, -1.3]
                                       (union
                                        (color-green (translate [0.3, 2.3, -1.4] (cube 2, 1, 1.5)))
                                        (color-yellow (translate [0.3, 7, -1.4] (cube 2, 1, 1.5))))))
                          ; socket holder barrier

                          (translate [0, 0, -1.3]
                                     (color-red
                                      (translate [-4.5, 0.8, 0]
                                                 (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder 0.3 6)))))

                                     (color-green
                                      (translate [2.3, 2.6, 0]
                                                 (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder 0.3 6.0)))))
                                     (color-yellow
                                      (translate [1.3, 6.9, 0]
                                                 (rotate [0, (deg2rad 90), 0] (binding [*fn* 20] (cylinder 0.3 8.2)))))))
               ; end union
               )


              ; hot-swap socket hole
              (translate [0.075 4.815 (- -2.75 socket-height-adjust)]
                         (union
                          (translate [2.475 0.325 0]
                                     (binding [*fn* 50] (cylinder hot-swap-radius 20)))
                          (translate [-3.875 -2.215 0]
                                     (binding [*fn* 50] (cylinder hot-swap-radius 20)))))
              ; keyboard center hole
              (translate [0, 0, -3] (binding [*fn* 20] (cylinder 2.3 3.2)))
              ; 5ft - socket holes
              (translate [-5.08 0 -3] (binding [*fn* 20] (cylinder 1.1 3.2)))
              (translate [5.08 0 -3] (binding [*fn* 20] (cylinder 1.1 3.2)))

              ;half hole
              (translate [0 (/ (+ keyswitch-width 3) -3) (- -2.05 socket-height-adjust)]
                         (cube (+ keyswitch-height 3.6) (/ (+ keyswitch-width 3) 3) 3.1))

              ;corner cut

              (translate [10, 9, -2.5]
                         (rotate (deg2rad 45) [0, 0, 1] (cube 5, 5, 8)))
              (translate [-10, 9, -2.5]
                         (rotate (deg2rad 45) [0, 0, 1] (cube 5, 5, 8)))
              ;(binding [*fn* 50] (cylinder 2 2))
              )
             ; end difference
             ))

(defn amoeba [thickness mountHole]
  (let [height                   18.2
        width1                   17.58
        width2                   18.77
        ;thickness                1.65

        screwHoleDiameter        2.44

        screwHoleTopOffset       9.65

        sideHeight               7.02
        verticalOffset           -1

        centerDiameter           6]

    (translate [0, 0, (- -0.50 thickness)]
               (difference
                (union
                 (cube width1, 18.2, thickness)
                 (translate [0, verticalOffset, 0] (cube width2, sideHeight, thickness))

                 (if mountHole
                   (translate [0, verticalOffset, 0]

                              (translate [(/ width2 -2), 0, 0]
                                         (binding [*fn* 20] (cylinder (/ screwHoleDiameter 2) (+ thickness 6))))
                              (translate [(/ width2 2), 0, 0]
                                         (binding [*fn* 20] (cylinder (/ screwHoleDiameter 2) (+ thickness 6))))))



                 )

                (if (= mountHole false)
                  (translate [0, verticalOffset, 0]

                             (translate [(/ width2 -2), 0, 0]
                                        (binding [*fn* 20] (cylinder (/ screwHoleDiameter 2) thickness)))
                             (translate [(/ width2 2), 0, 0]
                                        (binding [*fn* 20] (cylinder (/ screwHoleDiameter 2) thickness)))))


                (binding [*fn* 20] (cylinder (/ centerDiameter 2), 5))
                ; hot-swap socket hole

                (translate [0.075 4.815 (- -2.75 socket-height-adjust)]
                           (union
                            (translate [2.475 0.325 0]
                                       (binding [*fn* 20] (cylinder hot-swap-radius 20)))
                            (translate [-3.875 -2.215 0]
                                       (binding [*fn* 20] (cylinder hot-swap-radius 20)))))
                ; 5ft - socket holes
                (translate [-5.08 0 -3] (binding [*fn* 20] (cylinder 1.1 13.2)))
                (translate [5.08 0 -3] (binding [*fn* 20] (cylinder 1.1 13.2)))
                ;end dif
                ))))

(def amoeba-model
  (let [thickness                1.65]
    (amoeba thickness false)))

(def amoeba-hole
  (let [thickness                2]
    (translate [0, 0, switch-z-offset]
               (amoeba thickness true))))

(def amoeba-holder
  (let [zOffset                   (* -1 socket-height-adjust)
        width2                    18.77
        holderXOffsetRight        (/ width2 2)
        holderXOffsetLeft         (* -1 holderXOffsetRight)
        screwRad                  (/ 2.2 2)
        amebaMountDiameter        3.0
        amebaMountRad             (/ amebaMountDiameter 2)
        amebaMountHeight          3
        amebaScrewHoleHeight      5
        verticalOffset            -1]
    (translate [0, 0, 0.7]
               (difference
                (union
                 ; hot-swap plate
                 (translate [0, 0, zOffset]


                            (translate [holderXOffsetRight, verticalOffset, 0.5]
                                       (scale [1, 1.5, 1]
                                              (color-red (binding [*fn* 20] (cylinder amebaMountRad amebaMountHeight)))))
                            (translate [holderXOffsetLeft, verticalOffset, 0.5]
                                       (scale [1, 1.5, 1]
                                              (color-blue (binding [*fn* 20] (cylinder amebaMountRad amebaMountHeight)))))


                            (difference
                             (color-yellow
                              (cube (+ keyswitch-height 3.4) (+ keyswitch-width 3) (+ socket-thickness socket-height-adjust)))
                             (translate [0 0 -0.5]
                                        (cube keyswitch-height keyswitch-width (+ socket-height-adjust 2.2)))
                             ; end dif
                             )

                            ;end zOffset
                            )
                 ; end dif
                 )
                (translate [holderXOffsetRight, verticalOffset, 0]
                           (color-red (binding [*fn* 20] (cylinder screwRad amebaScrewHoleHeight))))
                (translate [holderXOffsetLeft, verticalOffset, 0]
                           (color-blue (binding [*fn* 20] (cylinder screwRad amebaScrewHoleHeight))))))))


(def hot-socket
  (translate [0, 0, switch-z-offset]
             (case hot-swap
               0 hot-socket-standart
               1 hot-socket-standart
               2 hot-socket-low-profile
               3 (union amoeba-holder))))
