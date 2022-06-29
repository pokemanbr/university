(def constant constantly)
(defn variable [letter] (fn [vars] (vars letter)))

(defn operation [f]
  (fn [& ops]
    (fn [vars] (apply f (mapv #(% vars) ops)))))

(defn sqr [x] (* x x))

(def div (fn
           ([op] (/ 1.0 (double op)))
           ([op & ops] (/ (double op) (double (apply * ops))))))
(def meanOperation (fn [& ops] (/ (apply + ops) (count ops))))
(def varnOperation (fn [& ops] (-
                                 (/ (apply + (map #(sqr %) ops)) (count ops))
                                 (sqr (/ (apply + ops) (count ops))))))

(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def divide (operation div))
(def negate subtract)
(def mean (operation meanOperation))
(def varn (operation varnOperation))

(def functionalOperations {'+      add
                           '-      subtract
                           '*      multiply
                           '/      divide
                           'negate negate
                           'mean   mean
                           'varn   varn})

(defn parseExpression [operations const Var exps]
  (cond
    (seq? exps) (apply (operations (first exps)) (map (partial parseExpression operations const Var) (rest exps)))
    (number? exps) (const exps)
    (symbol? exps) (Var (str exps))))
(defn parser [exp operations const Var] (parseExpression operations const Var (read-string exp)))

(defn parseFunction [exp] (parser exp functionalOperations constant variable))

;===========================================================================================;

(definterface Expression
  (evaluate [vars])
  (toString [])
  (diff [name]))

(declare ZERO)

(deftype ConstantConstructor [num]
  Expression
  (evaluate [this vars] num)
  (toString [this] (str num))
  (diff [this name] ZERO))

(defn Constant [num] (ConstantConstructor. num))

(def ZERO (ConstantConstructor. 0))
(def ONE (ConstantConstructor. 1))
(def TWO (ConstantConstructor. 2))

(deftype VariableConstructor [name]
  Expression
  (evaluate [this vars] (vars name))
  (toString [this] name)
  (diff [this x] (if (= x name) ONE ZERO)))

(defn Variable [name] (VariableConstructor. name))

(deftype Operation [f name diffOp args]
  Expression
  (evaluate [this vars] (apply f (mapv #(.evaluate % vars) args)))
  (toString [this] (str "(" name " " (clojure.string/join " " args) ")"))
  (diff [this name] (diffOp args name)))

(defn createObject [f name diffOp] (fn [& args] (Operation. f name diffOp args)))

(defn evaluate [exp vars] (.evaluate exp vars))
(defn toString [exp] (.toString exp))
(defn diff [exp name] (.diff exp name))

(declare Add Subtract Multiply Divide Negate Mean Varn)

(defn diffArgs [x name] (mapv #(diff % name) x))

(defn diff-div [x y name] (Divide (Subtract ((diff x name) y) (x (diff y name))) (Multiply y y)))
(defn diff-mean [x name] (Divide (apply Add (diffArgs x name)) (Constant (count x))))
(defn diff-multi [x name] (if (= 1 (count x))
                            (diff (first x) name)
                            (Add (Multiply (diff (first x) name) (apply Multiply (rest x)))
                                 (Multiply (first x) (diff-multi (rest x) name)))))

(def Add (createObject + "+" (fn [x name] (apply Add (diffArgs x name)))))
(def Subtract (createObject - "-" (fn [x name] (apply Subtract (diffArgs x name)))))
(def Multiply (createObject * "*" (fn [x name] (diff-multi x name))))
(def Divide (createObject div "/" (fn [x name] (if (= 1 (count x))
                                                 (Divide (Negate (diff (first x) name)) (Multiply (first x) (first x)))
                                                 (Divide (Subtract (Multiply
                                                                     (diff (first x) name)
                                                                     (apply Multiply (rest x)))
                                                                   (Multiply
                                                                     (first x)
                                                                     (diff-multi (rest x) name)))
                                                         (Multiply
                                                           (apply Multiply (rest x))
                                                           (apply Multiply (rest x))))))))
(def Negate (createObject - "negate" (fn [x name] (apply Negate (diffArgs x name)))))
(def Mean (createObject meanOperation "mean" (fn [x name] (diff-mean x name))))
(def Varn (createObject varnOperation "varn" (fn [x name] (Subtract
                                                            (Multiply TWO
                                                                      (Divide
                                                                        (apply Add (mapv Multiply x (diffArgs x name)))
                                                                        (Constant (count x))))
                                                            (Multiply TWO (Multiply
                                                                            (apply Mean (diffArgs x name))
                                                                            (apply Mean x)))))))

(def objectOperations {'+      Add
                       '-      Subtract
                       '*      Multiply
                       '/      Divide
                       'negate Negate
                       'mean   Mean
                       'varn   Varn})

(defn parseObject [exp] (parser exp objectOperations Constant Variable))