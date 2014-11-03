(ns edin.drawing)

; (drawText ctx 10 10 "Here goes something" 12 "then again something"
(defn drawText
  [ctx x y what]
  (.fillText ctx (apply str what) x y))