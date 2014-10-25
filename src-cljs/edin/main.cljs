(ns edin.main
  (:require [goog.dom :as dom]
            [goog.events :as events]))

(def NAME "edin text editor")
(def editor-canvas (dom/getElement "editor"))
(def editor-ctx (.getContext editor-canvas "2d"))

(def cx 0)
(def cy 0)

(def width 600)
(def height 600)

(def caret-width 2)
(def caret-height 15)

(def pos-y 20)
(def w (dom/getWindow))
(def d (dom/getDocument))
(set! (.-font editor-ctx) "15px Monospace")
(def char-width (.-width (.measureText editor-ctx "a")))
(def buffer "")

(defn get-lines 
  []
  (.split buffer "\n"))
(defn num-of-lines
  []
  (count (get-lines)))

(defn len-at-line
  [n]
  (let [line (nth (get-lines) n)]
    (count line)))


(defn move-x-to 
  [x]
  (set! cx x))

(defn move-y-to
  [y]
  (set! cy y))

(defn inc-x 
  []
  (move-x-to (+ cx 1)))

(defn dec-x
  []
  (move-x-to (- cx 1)))

(defn inc-y
  []
  (move-y-to (+ cy 1)))

(defn dec-y
  []
  (move-y-to (- cy 1)))

; move x forward only if x is not greater then len of current line
(defn move-x-forward 
  []
  (when (< (+ cx 1) (len-at-line cy)
           (inc-x))))

(defn move-y-backward
  []
  (when (>= (- cy 1) 0 )
    (dec-y)))
; move x backward; if not try to move y backward
(defn move-x-backward 
  []
  (if (< (- cx 1) 0)
    (move-y-backward)
    (dec-x)))


; check for number of lines
(defn move-y-forward 
  []
  (inc-y))

(defn get-cursor-drawing-pos
  []
  {:x (+ 20 (* (+ cx 1)   char-width))  :y (* (+ cy 1) 15)})


(def keycode-to-string {
                        32 (fn [buf] (str buf " "))
                        57 (fn [buf] (str buf "("))
                        48 (fn [buf] (str buf ")"))
                        219 (fn [buf] (str buf "{"))
                        221 (fn [buf] (str buf "}"))
                        16 (fn [buf] buf)
                        222 (fn [buf] (str buf "\""))
                        91 (fn [buf] (str buf "'"))
                        186 (fn [buf] (str buf ";"))
                        187 (fn [buf] (str buf "="))
                        190 (fn [buf] (str buf "."))
                        8 (fn [buf] (move-x-backward) (reduce str (take (- (count buf) 1) buf)))
                        13 (fn [buf] (move-y-forward) (move-x-to 0) (str buf  "\n"))
                        37 (fn [buf] (move-x-backward) buf)
                        39 (fn [buf] (move-x-forward) buf)
                       ; 9 #(str % "\t")
                        })

(defn handle-input
  [keyCode e]
  (do 
       (set! buffer (str buffer (.fromCharCode js/String (+ 32 keyCode))))     
       (move-x-forward)))

(defn render-editor-ui 
  []
  (.strokeRect editor-ctx 0 0 600 600)
  (.fillText editor-ctx (str cx "," cy) 20 580))

(defn draw-cursor
  []
  (let [x (:x (get-cursor-drawing-pos))
        y (:y (get-cursor-drawing-pos)) ]
    
    (.fillRect editor-ctx x y caret-width caret-height)))

(render-editor-ui)
(defn render
  []
  (do
    (.clearRect editor-ctx 0 0 600 600)
    (render-editor-ui)
    (.log js/console (str "rendering!" buffer))

    (let [lines (.split buffer "\n")
          text-width (.-width (.measureText editor-ctx (last lines))) ]
      (loop [y 20 
             rec-lines lines]
        (when (seq rec-lines)
          (.fillText editor-ctx (first rec-lines) 20 y)
          (set! pos-y (+ y 20))
          (recur pos-y (rest rec-lines))))
      (draw-cursor))))

(defn on-input
  [e]
  (let [code (.-keyCode e)
        handler (get keycode-to-string code :handler-not-found) ]
    (.log js/console (str "key down: " code) e)
    (.preventDefault e)
    (.stopPropagation e)
     (when (= handler :handler-not-found)
       (handle-input code e))    
     (when (not (= handler :handler-not-found))
       (.log js/console "handling event")
       (set! buffer (handler buffer)))
     (render)))

(events/listen d (.-KEYDOWN events/EventType) on-input)



