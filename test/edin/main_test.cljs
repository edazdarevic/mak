(ns edin.core-test
 (:require-macros [cemerick.cljs.test :as m :refer (is test-var deftest run-tests deftesthook)])
 (:require [cemerick.cljs.test :as t]
            [edin.buffer :as buffer]))


(deftest insert-to-empty-buffer
  (let [b (buffer/create-buffer "")]
    (is (= "e" (buffer/insert b 0 "e")))))

(deftest insert-to-the-beginning-of-buffer
  (let [b (buffer/create-buffer "edin")]
    (is (= "Fedin" (buffer/insert b 0 "F")))))

(deftest insert-to-the-beginning-of-buffer-negative-index
  (let [b (buffer/create-buffer "edin")]
    (is (= "Fedin" (buffer/insert b -100 "F")))))

(deftest insert-to-the-end-of-buffer
  (let [b (buffer/create-buffer "edin")]
    (is (= "edinF" (buffer/insert b 4 "F")))))

(deftest insert-to-the-end-of-buffer-past-end
  (let [b (buffer/create-buffer "edin")]
    (is (= "edinF" (buffer/insert b 100 "F")))))

(deftest remove-from-buffer
  (let [b (buffer/create-buffer "edin")]
    (is (= "din" (buffer/remove-at b 0)))))

(deftest remove-from-buffer-2
  (let [b (buffer/create-buffer "edin")]
    (is (= "ein" (buffer/remove-at b 1)))))

(deftest remove-from-buffer-3
  (let [b (buffer/create-buffer "edin")]
    (is (= "en" (buffer/remove-at b 1 2)))))

(deftest remove-from-buffer-4
  (let [b (buffer/create-buffer "edin")]
    (is (= "edi" (buffer/remove-at b 3 100)))))

(deftest remove-from-buffer-5
  (let [b (buffer/create-buffer "edin")]
    (is (= "" (buffer/remove-at b 0 100)))))

(deftest count-of-lines
  (let [b (buffer/create-buffer "edin\ndazdarevic")
        lines  (buffer/to-lines b)]
    (is (=
      4
      (first (buffer/len-of-each-line lines))))
    (is (=
      10
      (last (buffer/len-of-each-line lines))))))

(deftest cursor-position
  (let [b (buffer/create-buffer "edin\ndazdarevic")
   position (buffer/buffer-position-to-cursor (buffer/to-lines b) 7)]
   (is ( = (:line position) 1))
   (is ( = (:col position) 2))
   ))

(deftest cursor-position-2
  (let [b (buffer/create-buffer "edin\ndazdarevic")
   position (buffer/buffer-position-to-cursor (buffer/to-lines b) 9)]
   (is ( = (:line position) 1))
   (is ( = (:col position) 4))
   ))

(deftest cursor-position-3
  (let [b (buffer/create-buffer "edin\ndazdarevic")
   position (buffer/buffer-position-to-cursor (buffer/to-lines b) 3)]
   (is ( = (:line position) 0))
   (is ( = (:col position) 3))

   ))

(deftest get-position
  (let [b (buffer/create-buffer "abcd\nefghi")
        lines (buffer/to-lines b)
        wrapped (buffer/word-wrap-lines lines 3)
        sizes (buffer/len-of-each-line wrapped)
        pos (buffer/get-position sizes 5)
        ]
        (is (= (:line pos) 2))
        (is (= (:col pos) 1))
        ))

(deftest get-position-wordwrapped
  (let [b (buffer/create-buffer "edin\ndazdarevicabcd")
        pos (buffer/position-to-cursor-wrapped b 10 5)]

        (is (= (:line pos) 2))
        (is (= (:col pos)  0))))

(deftest get-position-wordwrapped2
  (let [b (buffer/create-buffer "edin\ndazdarevicabcd")
        pos (buffer/position-to-cursor-wrapped b 15 5)]

        (is (= (:line pos) 3))
        (is (= (:col pos)  0))))

(deftest get-position-wordwrapped3
  (let [b (buffer/create-buffer "edind\ndazdarevicabcd")
        pos (buffer/position-to-cursor-wrapped b 10 5)]

        (is (= (:line pos) 2))
        (is (= (:col pos)  4))))


(deftest get-position-wordwrapped4
  (let [b (buffer/create-buffer "edind\ndazdarevicabcd")
        pos (buffer/position-to-cursor-wrapped b 16 5)]

        (is (= (:line pos) 4))
        (is (= (:col pos)  0))))

(deftest get-position-wordwrapped5
  (let [b (buffer/create-buffer "edin\n")
        pos (buffer/position-to-cursor-wrapped b 2 5)]

        (is (= (:line pos) 0))
        (is (= (:col pos)  2))))

(deftest get-position-wordwrapped6
  (let [b (buffer/create-buffer "edin\n")
        pos (buffer/position-to-cursor-wrapped b 0 5)]

        (is (= (:line pos) 0))
        (is (= (:col pos)  0))))

(deftest get-position-wordwrapped7
  (let [b (buffer/create-buffer "edin\na")
        pos (buffer/position-to-cursor-wrapped b 4 5)]

        (is (= (:line pos) 0))
        (is (= (:col pos)  4))))

(deftest get-position-wordwrapped8
  (let [b (buffer/create-buffer "e\nedin")
        pos (buffer/position-to-cursor-wrapped b 4 5)]

        (is (= (:line pos) 1))
        (is (= (:col pos)  2))))

(deftest get-position-wordwrapped9
  (let [b (buffer/create-buffer "e\nedin")
        pos (buffer/position-to-cursor-wrapped b 4 2)]

        (is (= (:line pos) 2))
        (is (= (:col pos)  0))))

(deftest move-left1
  (let [b (buffer/create-buffer "edin")
        pos 3
        new-pos (buffer/move-left b pos)]
        (is (= 2 new-pos))))


(deftest move-left2
  (let [b (buffer/create-buffer "edin")
        pos 4
        new-pos (buffer/move-left b pos)]
        (is (= 3 new-pos))))

(deftest move-left3
  (let [b (buffer/create-buffer "edin")
        pos 1
        new-pos (buffer/move-left b pos)]
        (is (= 0 new-pos))))

(deftest move-left4
  (let [b (buffer/create-buffer "edin")
        pos 0
        new-pos (buffer/move-left b pos)]
        (is (= 0 new-pos))))

(deftest move-right
  (let [b (buffer/create-buffer "edin")
        pos 0
        new-pos (buffer/move-right b pos)]
        (is (= 1 new-pos))))

(deftest move-right2
  (let [b (buffer/create-buffer "edin")
        pos 1
        new-pos (buffer/move-right b pos)]
        (is (= 2 new-pos))))

(deftest move-right3
  (let [b (buffer/create-buffer "edin")
        pos 3
        new-pos (buffer/move-right b pos)]
        (is (= 4 new-pos))))

(deftest move-right4
  (let [b (buffer/create-buffer "edin")
        pos 4
        new-pos (buffer/move-right b pos)]
        (is (= 4 new-pos))))

(deftest move-and-position
  (let [b (buffer/create-buffer "edin\ndazdarevic")
        pos 4
        new-pos (buffer/move-right b pos)
        coords (buffer/buffer-position-to-cursor (buffer/to-lines b) new-pos)]
        (is (= (:line coords) 1))
        (is (= (:col coords) 0))
        ))
(deftest idx-of-negative
  (let [b (buffer/create-buffer "edin\ndazdarevic")
        a [3 1 9]
        idx (buffer/get-index-of-negative a 7)
        sum (buffer/sum-up-to a idx)]
   (is ( = idx 2))
   (is ( = sum 4))

   ))

(deftest ensure-bounds1
  (let [value1 (buffer/ensure-bounds 0 100 101)
        value2 (buffer/ensure-bounds 0 100 100)
        value3 (buffer/ensure-bounds 0 100 -1)
        value4 (buffer/ensure-bounds 0 100 50)
        value5 (buffer/ensure-bounds 50 100 49)]
    (is (= value1 100))
    (is (= value2 100))
    (is (= value3 0))
    (is (= value4 50))
    (is (= value5 50))
    )
  )

(deftest prev-newline
  (let [b (buffer/create-buffer "edin\ndazdarevic")
        newline-index (buffer/prev-newline-index b 7)]
        (is (= newline-index 4))
    ))

(deftest word-wrap-test
  (let [b (buffer/create-buffer "edindazdarevic")
    wrapped (buffer/word-wrap b 4)]
    (is (= 4 (count wrapped)))
    (is (= "edin" (nth wrapped 0)))
    (is (= "dazd" (nth wrapped 1)))
    (is (= "arev" (nth wrapped 2)))
    (is (= "ic" (nth wrapped 3)))))












