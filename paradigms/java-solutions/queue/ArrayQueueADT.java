package queue;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

/*
    Model: a[1]..a[n]
    Invariant: n >= 0 && for (i=1..n : a[i] != null)

    Let immutable(n, m): for (i=n..m : a'[i] == a[i])
 */

public class ArrayQueueADT {
    private int size = 0, head = 0;
    private Object[] elements = new Object[1];
    private Map<Object, Integer> numbers = new HashMap<Object, Integer>();

//    Pred: element != null && queue != null
//    Post: n' == n + 1 && a'[n'] == element && immutable(1, n)
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null : "You can't enqueue element null";

        ensureCapacity(queue, queue.size + 1);
        increase(queue, element);
        queue.elements[(queue.head + queue.size) % queue.elements.length] = element;
        queue.size++;
    }

    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            Object[] newElements = new Object[capacity * 2];
            System.arraycopy(queue.elements, queue.head, newElements, 0, queue.elements.length - queue.head);
            System.arraycopy(queue.elements, 0, newElements, queue.elements.length - queue.head, queue.head);
            queue.head = 0;
            queue.elements = newElements;
        }
    }

//    Pred: n >= 1 && queue != null
//    Post: R == a[1] && immutable(1, n) && n == n'
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0 : "You can't get element when queue is empty";

        return queue.elements[queue.head];
    }

//    Pred: n >= 1 && queue != null
//    Post: R == a[1] && n' == n - 1 && immutable(2, n)
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0 : "You can't dequeue when queue is empty";

        Object result = queue.elements[queue.head];
        decrease(queue, result);
        queue.head = (queue.head + 1) % queue.elements.length;
        queue.size--;
        return result;
    }

//    Pred: queue != null
//    Post: R == n && n' == n && immutable(1, n)
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

//    Pred: queue != null
//    Post: R == (n == 0) && n' == n && immutable(1, n)
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

//    Pred: queue != null
//    Post: n' == 0
    public static void clear(ArrayQueueADT queue) {
        queue.size = 0;
        queue.head = 0;
        queue.elements = new Object[1];
        queue.numbers.clear();
    }

//    Pred: element != null && queue != null
//    Post: n' == n + 1 && for (i=2..n' : a'[i] == a[i - 1]) && a[1] == element
    public static void push(ArrayQueueADT queue, Object element) {
        assert element != null : "You can't push element null";

        ensureCapacity(queue,queue.size + 1);
        increase(queue, element);
        queue.elements[((queue.head - 1) + queue.elements.length) % queue.elements.length] = element;
        queue.head = ((queue.head - 1) + queue.elements.length) % queue.elements.length;
        queue.size++;
    }

//    Pred: n >= 1 && queue != null
//    Post: R == a[n] && immutable(1, n) && n == n'
    public static Object peek(ArrayQueueADT queue) {
        assert queue.size > 0 : "You can't get peek when queue is empty";

        return queue.elements[(queue.head + queue.size - 1) % queue.elements.length];
    }

//    Pred: n >= 1 && queue != null
//    Post: R == a[1] && n' == n - 1 && immutable(1, n')
    public static Object remove(ArrayQueueADT queue) {
        assert queue.size > 0 : "You can't remove when queue is empty";

        Object result = queue.elements[(queue.head + queue.size - 1) % queue.elements.length];
        decrease(queue, result);
        queue.elements[(queue.head + queue.size - 1) % queue.elements.length] = null;
        queue.size--;
        return result;
    }

//    Pred: element != null && queue != null
//    Post: (R == |{ i in Z : i in [1, n], a[i] == element }|) && immutable(1, n) && n == n'
    public static int count(ArrayQueueADT queue, Object element) {
        return queue.numbers.getOrDefault(element, 0);
    }

    private static void increase(ArrayQueueADT queue, Object element) {
        queue.numbers.put(element, queue.numbers.getOrDefault(element, 0) + 1);
    }

    private static void decrease(ArrayQueueADT queue, Object element) {
        queue.numbers.put(element, queue.numbers.get(element) - 1);
    }
}