(ns edin.drawing)

; (drawText ctx 10 10 "Here goes something" 12 "then again something"
(defn drawText
  [ctx x y what]
  (.fillText ctx (apply str what) x y))

(defn clearRect
  [ctx w h]
  (.clearRect ctx 0 0 w h))

(defn fillRect
  ([ctx x y w h]
    (.fillRect ctx x y w h))
  ([ctx w h]
    (.fillRect ctx 0 0 w h)))

(defn fillStyle!
  [ctx style]
  (set! (.-fillStyle ctx) style))

(defn textBaseline!
  [ctx baseline]
  (set! (.-textBaseline ctx) baseline))

(defn measureText
  [ctx text]
  (.-width (.measureText ctx text)))

(defn font!
  [ctx f]
  (set! (.-font ctx) f))


