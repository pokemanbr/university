(defn eq-size-v [& vs] (apply = (mapv count vs)))
(defn eq-size-m [& ms] (apply eq-size-v ms))

(defn s? [s] (number? s))
(defn v? [v] (or (and (coll? v) (empty? v)) (and (vector? v) (every? s? v))))
(defn m? [m] (and (vector? m) (every? v? m) (apply eq-size-v m)))
(defn x? [x] (or (v? x) (and (== (count x) (count (first x))) (every? x? x))))

(defn vf [f] (fn [& vs]
                 {:pre  [(every? v? vs) (apply eq-size-v vs)]
                  :post [(v? %) (eq-size-v % (first vs))]}
                 (if (= 1 (count vs))
                   (mapv f (first vs))
                   (apply (partial mapv f) vs))) )

(def v+ (vf +))
(def v- (vf -))
(def v* (vf *))
(def vd (vf /))

(defn scalar [& vs]
      {:pre  [(every? v? vs) (apply eq-size-v vs)]
       :post [(s? %)]}
      (apply + (apply v* vs)))
(defn vect [& vs]
      {:pre  [(every? v? vs) (apply eq-size-v vs) (= 3 (count (first vs)))]
       :post [(v? %) (= 3 (count %))]}
      (if (= 1 (count vs))
        (first vs)
        (reduce #(vector
                   (- (* (nth %1 1) (nth %2 2)) (* (nth %1 2) (nth %2 1)))
                   (- (* (nth %1 2) (nth %2 0)) (* (nth %1 0) (nth %2 2)))
                   (- (* (nth %1 0) (nth %2 1)) (* (nth %1 1) (nth %2 0))))
                vs)))

(defn v*s [v & ss]
      {:pre  [(v? v) (every? s? ss)]
       :post [(v? %) (eq-size-v % v)]}
      (mapv (partial * (apply * ss)) v))

(defn mf [f] (fn [& ms]
                 {:pre  [(every? m? ms) (apply eq-size-m ms)]
                  :post [(m? %) (eq-size-m % (first ms))]}
                 (if (= 1 (count ms))
                   (mapv f (first ms))
                   (apply (partial mapv f) ms))))

(def m+ (mf v+))
(def m- (mf v-))
(def m* (mf v*))
(def md (mf vd))

(defn transpose [m]
      {:pre  [(m? m)]}
      (apply (partial mapv vector) m))

(defn m*s [m & ss]
      {:pre  [(m? m) (every? s? ss)]
       :post [(m? %) (eq-size-m % m)]}
      (mapv #(apply (partial v*s %) ss) m))
(defn m*v [m vs]
      {:pre  [(m? m) (v? vs)]
       :post [(v? %) (eq-size-v % m) (mapv #(eq-size-v % vs) m)]}
      (mapv #(scalar vs %) m))
(defn m*m [& ms]
      {:pre  [(every? m? ms)]
       :post [(m? %) (= (count %) (count (first ms))) (= (count (first %)) (count (first (last ms))))]}
      (if (= 1 (count ms))
        (first ms)
        (reduce #(mapv (fn [v] (m*v (transpose %2) v)) %1) ms)) )

(defn xf [f] (fn eval [& xs]
                 ;{:pre  [(every? x? xs) ]
                 ; :post [(x? %)]}
                 (if (every? s? xs)
                   (apply f xs)
                   (apply mapv eval xs))))



(def x+ (xf +))
(def x- (xf -))
(def x* (xf *))
(def xd (xf /))