(ns edin.main
  (:require [goog.dom :as dom]
            [goog.events :as events])
  )

(def NAME "edin text editor")
(def drawing-position {:x 10 :y 20})

(def editor-canvas (dom/getElement "editor"))
(def editor-ctx (.getContext editor-canvas "2d"))
(def w (dom/getWindow))
(def d (dom/getDocument))
(set! (.-font editor-ctx) "15px Monospace")
(def char-width (.-width (.measureText editor-ctx "a")))
(def buffer "")
;(defn render-cursor

;)

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
                        8 (fn [buf](reduce str (take (- (count buf) 1) buf)))
                        })

(def lines [])


;(js/setInterval render-cursor 500)

(defn render
  []
  (do

    (.log js/console (str "rendering!" buffer))
    (.clearRect editor-ctx 0 0 600 600)
    (.fillText editor-ctx buffer 20 20)
    (let [text-width (.-width (.measureText editor-ctx buffer))]
      (.fillRect editor-ctx (+ 20 text-width) 8 char-width 15)
      )
    )
  )

;(.log js/console (get keycode-to-string 32))
(defn on-input
  [e]
  ;(js/alert (.-keyCode e))
  (let [code (.-keyCode e)
        pos-x (:x drawing-position)
        pos-y (:y drawing-position)
        handler (get keycode-to-string code :handler-not-found)
        ]
    (.log js/console (str "key down: " code) e)
    (.preventDefault e)
    (.stopPropagation e)
    ;(
     (when (= handler :handler-not-found)
       (set! buffer (str buffer (.fromCharCode js/String (+ 32 code)) ) ) ; just append to buffer from char code
       ;(

        ;(set! buffer (handler buffer))
        ;(.preventDefault e)
        ;(.stopPropagation e)
        ;)
       )  

     (when (not (= handler :handler-not-found))
       (.log js/console "handling event")
       (set! buffer (handler buffer))
       )
     (render)
    )
  )

(events/listen d (.-KEYDOWN events/EventType) on-input)



