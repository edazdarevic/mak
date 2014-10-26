(ns edin.main
  (:require [goog.dom :as dom]
            [goog.events :as events]))

(def NAME "edin text editor")
(def editor-canvas (dom/getElement "editor"))
(def editor-ctx (.getContext editor-canvas "2d"))

(def cx 1)
(def cy 1)

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

(set! (.-textBaseline editor-ctx) "top")

(defn get-lines 
  []
  (.split buffer "\n"))

(defn num-of-lines
  []
  (count (get-lines)))

(defn len-at-line
  [n]
  (let [line (nth (get-lines) (- n 1))]
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
  (if (<= (+ cx 0) (len-at-line cy))
           (inc-x)
           (when (<= (+ cy 1) (num-of-lines)) 
            (inc-y)
            (move-x-to 1)

            )
           ))

(defn xy-to-buffer-position
  []
  (let [temp-x (reduce + (map #(+ (count %) 1) (drop-last 1 (get-lines))))]
    (+ temp-x cx)
    )
  )
(defn move-y-backward
  []
  (when (>= (- cy 1) 1 )
    (dec-y)
    ; need to reposition x maybe
      (move-x-to (+ 1 (len-at-line cy)))
      (.log js/console (str "NEWPOSITON IN THE BUFFER IS" (xy-to-buffer-position)))
    ))
; move x backward; if not try to move y backward
(defn move-x-backward 
  []
  (if (< (- cx 1) 1)
    (move-y-backward)
    (dec-x)
    ))


; check for number of lines
(defn move-y-forward 
  []
  (inc-y))

(defn get-cursor-drawing-pos
  []
  {:x (+ 20 (* (- cx 1) char-width))  :y (* cy 20)})


(defn remove-at [index] (str (subs buffer 0 (- index 1)) (subs buffer (+ (- index 1) 1))))

(defn insert [index value] (str (subs buffer 0 (- index 1)) value (subs buffer (- index 1))))

(def keycode-to-string {
                        32 (fn [buf] 
                          (let [new-val (insert (xy-to-buffer-position) " ")]
                            (inc-x)
                            new-val
                            )
                          )
                        57 (fn [buf] (str buf "("))
                        48 (fn [buf] (str buf ")"))
                        219 (fn [buf] (str buf "{"))
                        221 (fn [buf] (str buf "}"))
                        16 (fn [buf] buf)
                        222 (fn [buf] (str buf "\""))
                        91 (fn [buf] (str buf "'"))
                        186 (fn [buf] (str buf ";"))
                        187 (fn [buf] (str buf "="))
            
                        8 (fn [buf]  
                          (let [new-val (remove-at (- (xy-to-buffer-position) 1) )]
                            (move-x-backward)
                            new-val
                            )

                          )
                        ; enter
                        13 (fn 
                          [buf] 
                          (let [new-val (insert (xy-to-buffer-position) "\n")]
                           (inc-y)
                           (move-x-to 1)
                           new-val
                          ))
                        37 (fn [buf] (move-x-backward) buf)
                        39 (fn [buf] (move-x-forward) buf)
                        })

(defn handle-input
  [keyCode e]
  (do 
      (.log js/console "xy-to-buffer-position" (xy-to-buffer-position))
       (set! buffer (insert (xy-to-buffer-position) (.fromCharCode js/String (+ 32 keyCode))))
       (inc-x)))

(defn render-editor-ui 
  []
  (.strokeRect editor-ctx 0 0 600 600)
  (.fillText editor-ctx (str "Line " cy ", Column " cx) 20 580))

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



