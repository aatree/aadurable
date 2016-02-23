(ns aadurable.cs
  (:require-macros
    [cljs.core :refer [es6-iterable]]))

(defn- -indexOf
  ([coll x]
   (-indexOf coll x 0))
  ([coll x start]
   (let [len (count coll)]
     (if (>= start len)
       -1
       (loop [idx (cond
                    (pos? start) start
                    (neg? start) (max 0 (+ start len))
                    :else start)]
         (if (< idx len)
           (if (= (nth coll idx) x)
             idx
             (recur (inc idx)))
           -1))))))

(defn- -lastIndexOf
  ([coll x]
   (-lastIndexOf coll x (count coll)))
  ([coll x start]
   (let [len (count coll)]
     (if (zero? len)
       -1
       (loop [idx (cond
                    (pos? start) (min (dec len) start)
                    (neg? start) (+ len start)
                    :else start)]
         (if (>= idx 0)
           (if (= (nth coll idx) x)
             idx
             (recur (dec idx)))
           -1))))))

(defprotocol x-iterator
  (xi-index [])
  (xi-bumpIndex [index])
  (xi-count [index])
  (xi-fetch [index]))

(deftype CountedSequence [arr i meta]
  Object
  (toString [coll]
    (pr-str* coll))
  (equiv [this other]
    (-equiv this other))
  (indexOf [coll x]
    (-indexOf coll x 0))
  (indexOf [coll x start]
    (-indexOf coll x start))
  (lastIndexOf [coll x]
    (-lastIndexOf coll x (count coll)))
  (lastIndexOf [coll x start]
    (-lastIndexOf coll x start))

  ICloneable
  (-clone [_] (CountedSequence. arr i meta))

  ISeqable
  (-seq [this]
    (when (< i (alength arr))
      this))

  IMeta
  (-meta [coll] meta)
  IWithMeta
  (-with-meta [coll new-meta]
    (CountedSequence. arr i new-meta))

  ASeq
  ISeq
  (-first [_] (aget arr i))
  (-rest [_] (if (< (inc i) (alength arr))
               (CountedSequence. arr (inc i) nil)
               (list)))

  INext
  (-next [_] (if (< (inc i) (alength arr))
               (CountedSequence. arr (inc i) nil)
               nil))

  ICounted
  (-count [_]
    (max 0 (- (alength arr) i)))

  IIndexed
  (-nth [coll n]
    (let [i (+ n i)]
      (when (< i (alength arr))
        (aget arr i))))
  (-nth [coll n not-found]
    (let [i (+ n i)]
      (if (< i (alength arr))
        (aget arr i)
        not-found)))

  ISequential
  IEquiv
  (-equiv [coll other] (equiv-sequential coll other))

  IIterable
  (-iterator [coll]
    (IndexedSeqIterator. arr i))

  ICollection
  (-conj [coll o] (cons o coll))

  IEmptyableCollection
  (-empty [coll] (.-EMPTY List))

  IReduce
  (-reduce [coll f]
    (array-reduce arr f (aget arr i) (inc i)))
  (-reduce [coll f start]
    (array-reduce arr f start i))

  IHash
  (-hash [coll] (hash-ordered-coll coll))

  IReversible
  (-rseq [coll]
    (let [c (-count coll)]
      (if (pos? c)
        (RSeq. coll (dec c) nil)))))

(es6-iterable IndexedSeq)
