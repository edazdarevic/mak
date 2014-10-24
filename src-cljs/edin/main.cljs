(ns edin.main
  (:require [goog.dom :as dom]
            [goog.events :as events])
  )

(def NAME "edin text editor")
;(def drawing-position {:x 10 :y 20})

(def pos-y 20)
(def editor-canvas (dom/getElement "editor"))
(def editor-ctx (.getContext editor-canvas "2d"))
(def w (dom/getWindow))
(def d (dom/getDocument))
(set! (.-font editor-ctx) "15px Monospace")
(def char-width (.-width (.measureText editor-ctx "a")))
(def buffer "")

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
                        13 #(str % "\n")
                        })

(defn render-editor-ui 
  []
  (.strokeRect editor-ctx 0 0 600 600))

(render-editor-ui)
(defn render
  []
  (do
    (.clearRect editor-ctx 0 0 600 600)
    (render-editor-ui)
    (.log js/console (str "rendering!" buffer))

    ;(.fillText editor-ctx buffer 20 20)
    (let [lines (.split buffer "\n")
          text-width (.-width (.measureText editor-ctx (last lines)))
          ]
      (loop [y 20 
             rec-lines lines]
        (when (seq rec-lines)
          (.fillText editor-ctx (first rec-lines) 20 y)
          (set! pos-y (+ y 20))
          (recur pos-y (rest rec-lines))
          )
        )
      (.fillRect editor-ctx (+ 20 text-width) (- pos-y 32 ) char-width 15)
      )
    )
  )

(defn on-input
  [e]
  ;(js/alert (.-keyCode e))
  (let [code (.-keyCode e)
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



