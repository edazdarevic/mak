(ns edin.main
  (:require [goog.dom :as dom]
            [goog.events :as events]))

(def NAME "edin text editor")
(def editor-canvas (dom/getElement "editor"))
(def editor-ctx (.getContext editor-canvas "2d"))

(def cx 1)
(def cy 1)

(def width 1200)
(def height 600)

(def caret-width 2)
(def caret-height 15)
(def margin-x 35)
(def margin-y 20)
(def tab-size 2)

(def pos-y margin-y)
(def w (dom/getWindow))
(def d (dom/getDocument))
(set! (.-font editor-ctx) "13px Monaco")
(def char-width (.-width (.measureText editor-ctx "a")))
(def buffer "")

(set! (.-textBaseline editor-ctx) "top")
(set! (.-fillStyle editor-ctx) "rgb(2, 36, 60)");

(defn get-lines []
  (.split buffer "\n"))

(defn num-of-lines []
  (count (get-lines)))

(defn len-at-line [n]
  (let [line (nth (get-lines) (- n 1))]
    (count line)))

(defn move-x-to [x]
  (set! cx x))

(defn move-y-to [y]
  (set! cy y))

(defn inc-x []
  (move-x-to (+ cx 1)))

(defn dec-x []
  (move-x-to (- cx 1)))

(defn inc-y []
  (move-y-to (+ cy 1)))

(defn dec-y []
  (move-y-to (- cy 1)))

; move x forward only if x is not greater then len of current line
(defn move-x-forward []
  (if (<= cx (len-at-line cy))
           (inc-x)
           (when (<= (+ cy 1) (num-of-lines)) 
            (inc-y)
            (move-x-to 1))))

(defn xy-to-buffer-position []
  (let [temp-x (reduce + (map #(+ (count %) 1) (drop-last 1 (take cy (get-lines)))))]
    (+ temp-x cx)))

(defn move-y-backward []
  (when (>= (- cy 1) 1 )
    (dec-y)
    (move-x-to (+ 1 (len-at-line cy)))))

; move x backward; if not try to move y backward
(defn move-x-backward []
  (if (< (- cx 1) 1)
    (move-y-backward)
    (dec-x)))

(defn move-y-forward []
  (inc-y))

(defn move-up []
  (when (>= (- cy 1) 1)
    (dec-y)
    (when (> cx (len-at-line cy))
      (move-x-to (+ 1 (len-at-line cy))))))

(defn move-down []
  (when (< cy (num-of-lines))
    (inc-y)
    (when (> cx (len-at-line cy))
      (move-x-to (+ 1 (len-at-line cy))))))

(defn get-cursor-drawing-pos []
  {:x (+ margin-x (* (- cx 1) char-width))  :y (* cy 20)})

(defn remove-at [index] 
  (str (subs buffer 0 (- index 1)) (subs buffer (+ (- index 1) 1))))

(defn insert [index value]
  (str (subs buffer 0 (- index 1)) value (subs buffer (- index 1))))

(defn insert-and-move [value]
    (let [new-val (insert (xy-to-buffer-position) value)]
      (dotimes [n (count value)]
        (inc-x))
      new-val))

(def keycode-to-string {
                        32 (fn [buf] 
                          (let [new-val (insert (xy-to-buffer-position) " ")]
                            (inc-x)
                            new-val
                            )
                          )
                        57 (fn [buf] (insert-and-move "("))
                        48 (fn [buf] (insert-and-move ")"))
                        219 (fn [buf] (insert-and-move "{"))
                        221 (fn [buf] (insert-and-move "}"))
                        16 (fn [buf] buf)
                        222 (fn [buf] (insert-and-move "\""))
                        91 (fn [buf] (insert-and-move "'"))
                        186 (fn [buf] (insert-and-move ";"))
                        187 (fn [buf] (insert-and-move "="))
                        190 (fn [buf] (insert-and-move "."))
                        8 (fn [buf]  
                          (let [new-val (remove-at (- (xy-to-buffer-position) 1) )]
                            (move-x-backward)
                            new-val
                            )

                          )
                        ; enter
                        189 (fn [buf] (insert-and-move "-"))
                        13 (fn 
                          [buf] 
                          (let [new-val (insert (xy-to-buffer-position) "\n")]
                           (inc-y)
                           (move-x-to 1)
                           new-val
                          ))
                        37 (fn [buf] (move-x-backward) buf)
                        39 (fn [buf] (move-x-forward) buf)
                        9 (fn 
                          [buf]
                            (insert-and-move (apply str (repeat tab-size " ")))
                          )
                        38 (fn [buf] (move-up) buf)
                        40 (fn [buf] (move-down) buf)
                        })

(defn draw-line-numbers []
  (loop [i 1]
    (.fillText editor-ctx (str i) 10 (+ margin-y (* (- i 1) 20)) )
    (when (< i (num-of-lines))
      (recur (+ i 1)))))

(defn count-trailing-whitespace [input]
  (count (take-while #(= % " ") (reverse input))))

(defn highlight-trailing-space []
  (dotimes [i (num-of-lines)]
    (let [line (nth (get-lines) (+ i 0))]
      (when (> (count-trailing-whitespace line) 0)
        (.save editor-ctx)
        (set! (.-fillStyle editor-ctx) "rgba(255, 0, 0, .7)")
        (.fillRect editor-ctx (+ margin-x (* (count line) char-width)) (+ margin-y (* (- i 0) 20)) char-width 20)
        (.restore editor-ctx)))))


(defn handle-input [keyCode e]
  (do 
    (.log js/console "xy-to-buffer-position" (xy-to-buffer-position))
    (set! buffer (insert (xy-to-buffer-position) (.fromCharCode js/String (+ 32 keyCode))))
    (inc-x)))



(defn render-editor-ui []
  (.strokeRect editor-ctx 0 0 width height)
  (set! (.-fillStyle editor-ctx) "rgb(2, 36, 60)");

  (.fillRect editor-ctx 0 0 width height)
  (set! (.-fillStyle editor-ctx) "rgb(255, 255, 255)");

  (.fillText editor-ctx (str "Line " cy ", Column " cx) 10 (- height 20))
  (set! (.-fillStyle editor-ctx) "rgb(96, 96, 96)");

  (draw-line-numbers)
  (highlight-trailing-space))

(defn draw-cursor []
  (let [x (:x (get-cursor-drawing-pos))
        y (:y (get-cursor-drawing-pos)) ]
    
    (.fillRect editor-ctx x y caret-width caret-height)))

(render-editor-ui)

(defn render []
  (do
    (.clearRect editor-ctx 0 0 width height)
    (render-editor-ui)
    (set! (.-fillStyle editor-ctx) "rgb(255, 255, 255)");

    (let [lines (.split buffer "\n")
          text-width (.-width (.measureText editor-ctx (last lines))) ]
      (loop [y margin-y 
             rec-lines lines]
        (when (seq rec-lines)
          (.fillText editor-ctx (first rec-lines) margin-x y)
          (set! pos-y (+ y 20))
          (recur pos-y (rest rec-lines))))
      (draw-cursor))))

(defn on-input [e]
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



