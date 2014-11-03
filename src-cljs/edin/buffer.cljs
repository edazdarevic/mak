(ns edin.buffer)

(defn create-buffer
  "Creates a new buffer"
  [buff]
  buff)

(defn insert
  "Inserts a value into a buffer"
  [buff idx value]
  (str
    (subs buff 0 idx)
    value
    (subs buff idx)))

(defn neg-or-zero?
  [item]
  (or (neg? item) (= item 0)))

(defn remove-at
  "Removes a substring from a buffer"
  ([buff idx] (remove-at buff idx 1))
  ([buff start-idx how-much]
    (str
      (subs buff 0 start-idx)
      (subs buff (+ how-much start-idx)))))

(defn indices [pred coll]
   (keep-indexed #(when (pred %2) %1) coll))

; (first (indices pos? [-1 0 99 100 101]))

(defn to-lines
  "Splits a buffer to a list of lines"
  [buff]
  (.split buff "\n"))

(defn len-of-each-line
  [lines]
  (map
    #(count %)
    lines))

(defn buffer-position-to-cursor
  [all-lines position]
  (let [
    sizes (len-of-each-line all-lines)

    lines (drop 1 (reduce
                    #(concat %1 [(- (last %1) %2) ] )
                    [position]
                        sizes))]

    (let [
      inds (indices neg-or-zero? lines)

          line-index (first inds)
          col (+ (nth lines line-index) (nth sizes line-index))
          ]
      { :line line-index
        :col (- col (* line-index 1))
        } )))
