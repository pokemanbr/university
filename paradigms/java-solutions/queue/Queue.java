package queue;

import java.util.function.Function;
import java.util.function.Predicate;

/*
    Model: a[1]..a[n]
    Invariant: n >= 0 && for (i=1..n : a[i] != null)

    Let immutable(n, m): for (i=n..m : a'[i] == a[i])
 */

public interface Queue {

//    Pred: element != null
//    Post: n' = n + 1 && a'[n'] == element && immutable(1, n)
    void enqueue(Object element);

//    Pred: n >= 1
//    Post: R == a[1] && immutable(1, n) && n == n'
    Object element();

//    Pred: n >= 1
//    Post: R == a[1] && n' == n - 1 && immutable(2, n)
    Object dequeue();

//    Pred: true
//    Post: R == n && n' == n && immutable(1, n)
    int size();

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(1, n)
    boolean isEmpty();

//    Pred: true
//    Post: n' == 0
    void clear();

    /*
        Model: result[1]..result[m]
        result = new Queue
        Invariant: m >= 0 && for (i=1..m : result[i] != null)
     */

//    Pred: predicate != null
//    Post:
    /*
            type(result) == type(a)
            result = {a[k_1], a[k_2], ..., a[k_m]}
            m == 0 || (for (i=1..(m - 1) : k_i < k_(i + 1)) && 0 < k_1 && k_m <= n)
            for(i=1..m : predicate(result[i]) == true)
            for(i=1..n : predicate(a[i]) == false && (0 == |{j in Z : j in [1..m], i == k_j}|) ||
            predicate(a[i]) == true && (0 < |{j in Z : j in [1..m], i == k_j}|))
     */
    Queue filter(Predicate<Object> predicate);

//    Pred: function != null
//    Post: type(result) == type(a) && m == n && for(i=1..m : result[i] == function(a[i])) && immutable(1, n) && n' == n
    Queue map(Function<Object, Object> function);
}
