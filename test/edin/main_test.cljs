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

(deftest cursor-position-2
  (let [b (buffer/create-buffer "edin\ndazdarevic")
   position (buffer/buffer-position-to-cursor (buffer/to-lines b) 3)]
   (is ( = (:line position) 0))
   (is ( = (:col position) 3))

   ))












