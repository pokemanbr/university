package queue;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

/*
    Model: a[1]..a[n]
    Invariant: n >= 0 && for (i=1..n : a[i] != null)

    Let immutable(n, m): for (i=n..m : a'[i] == a[i])
 */

public class ArrayQueueModule {
    private static int size = 0, head = 0;
    private static Object[] elements = new Object[1];
    private static Map<Object, Integer> numbers = new HashMap<Object, Integer>();

//    Pred: element != null
//    Post: n' == n + 1 && a'[n'] == element && immutable(1, n)
    public static void enqueue(Object element) {
        assert element != null : "You can't enqueue element null";

        ensureCapacity(size + 1);
        increase(element);
        elements[(head + size) % elements.length] = element;
        size++;
    }

    private static void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] newElements = new Object[capacity * 2];
            System.arraycopy(elements, head, newElements, 0, elements.length - head);
            System.arraycopy(elements, 0, newElements, elements.length - head, head);
            head = 0;
            elements = newElements;
        }
    }

//    Pred: n >= 1
//    Post: R == a[1] && immutable(1, n) && n == n'
    public static Object element() {
        assert size > 0 : "You can't get element when queue is empty";

        return elements[head];
    }

//    Pred: n >= 1
//    Post: R == a[1] && n' == n - 1 && immutable(2, n)
    public static Object dequeue() {
        assert size > 0 : "You can't dequeue when queue is empty";

        Object result = elements[head];
        decrease(result);
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return result;
    }

//    Pred: true
//    Post: R == n && n' == n && immutable(1, n)
    public static int size() {
        return size;
    }

//    Pred: true
//    Post: R == (n == 0) && n' == n && immutable(1, n)
    public static boolean isEmpty() {
        return size == 0;
    }

//    Pred: true
//    Post: n' == 0
    public static void clear() {
        size = 0;
        head = 0;
        elements = new Object[1];
        numbers.clear();
    }

//    Pred: element != null
//    Post: n' == n + 1 && for (i=2..n' : a'[i] == a[i - 1]) && a[1] == element
    public static void push(Object element) {
        assert element != null : "You can't push element null";

        ensureCapacity(size + 1);
        increase(element);
        elements[((head - 1) + elements.length) % elements.length] = element;
        head = ((head - 1) + elements.length) % elements.length;
        size++;
    }

//    Pred: n >= 1
//    Post: R == a[n] && immutable(1, n) && n == n'
    public static Object peek() {
        assert size > 0 : "You can't get peek when queue is empty";

        return elements[(head + size - 1) % elements.length];
    }

//    Pred: n >= 1
//    Post: R == a[1] && n' == n - 1 && immutable(1, n')
    public static Object remove() {
        assert size > 0 : "You can't remove when queue is empty";

        Object result = elements[(head + size - 1) % elements.length];
        decrease(result);
        elements[(head + size - 1) % elements.length] = null;
        size--;
        return result;
    }

//    Pred: element != null
//    Post: (R == |{ i in Z : i in [1, n], a[i] == element }|) && immutable(1, n) && n == n'
    public static int count(Object element) {
        return numbers.getOrDefault(element, 0);
    }

    private static void increase(Object element) {
        numbers.put(element, numbers.getOrDefault(element, 0) + 1);
    }

    private static void decrease(Object element) {
        numbers.put(element, numbers.get(element) - 1);
    }
}