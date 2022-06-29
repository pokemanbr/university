package queue;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

/*
    Model: a[1]..a[n]
    Invariant: n >= 0 && for (i=1..n : a[i] != null)

    Let immutable(n, m): for (i=n..m : a'[i] == a[i])
 */

public class ArrayQueue extends AbstractQueue {
    private int head = 0;
    private Object[] elements = new Object[1];
    private final Map<Object, Integer> numbers = new HashMap<Object, Integer>();

//    Pred: element != null
//    Post: n' == n + 1 && a'[n'] == element && immutable(1, n)
    @Override
    protected void enqueueImpl(Object element) {
        ensureCapacity(super.size + 1);
        increase(element);
        this.elements[(this.head + super.size) % this.elements.length] = element;
    }

    private void ensureCapacity(int capacity) {
        if (capacity > this.elements.length) {
            Object[] newElements = new Object[capacity * 2];
            System.arraycopy(this.elements, this.head, newElements, 0, this.elements.length - this.head);
            System.arraycopy(this.elements, 0, newElements, this.elements.length - this.head, this.head);
            this.head = 0;
            this.elements = newElements;
        }
    }

//    Pred: n >= 1
//    Post: R == a[1] && immutable(1, n) && n == n'
    protected Object elementImpl() {
        return elements[this.head];
    }

//    Pred: n >= 1
//    Post: R == a[1] && n' == n - 1 && immutable(2, n)
    protected Object dequeueImpl() {
        Object result = elements[this.head];
        decrease(result);
        elements[this.head] = null;
        this.head = (this.head + 1) % this.elements.length;
        return result;
    }

//    Pred: true
//    Post: n' == 0
    protected void clearImpl() {
        this.head = 0;
        this.elements = new Object[1];
        this.numbers.clear();
    }

//    Pred: element != null
//    Post: n' == n + 1 && for (i=2..n' : a'[i] == a[i - 1]) && a[1] == element
    public void push(Object element) {
        assert element != null : "You can't push element null";

        ensureCapacity(super.size + 1);
        increase(element);
        this.elements[((this.head - 1) + this.elements.length) % this.elements.length] = element;
        this.head = ((this.head - 1) + this.elements.length) % this.elements.length;
        super.size++;
    }

//    Pred: n >= 1
//    Post: R == a[n] && immutable(1, n) && n == n'
    public Object peek() {
        assert super.size > 0 : "You can't get peek when queue is empty";

        return this.elements[(this.head + super.size - 1) % this.elements.length];
    }

//    Pred: n >= 1
//    Post: R == a[1] && n' == n - 1 && immutable(1, n')
    public Object remove() {
        assert super.size > 0 : "You can't remove when queue is empty";

        Object result = this.elements[(this.head + super.size - 1) % this.elements.length];
        decrease(result);
        this.elements[(this.head + super.size - 1) % this.elements.length] = null;
        super.size--;
        return result;
    }

//    Pred: element != null
//    Post: (R == |{ i in Z : i in [1, n], a[i] == element }|) && immutable(1, n) && n == n'
    public int count(Object element) {
        return this.numbers.getOrDefault(element, 0);
    }

    private void increase(Object element) {
        this.numbers.put(element, this.numbers.getOrDefault(element, 0) + 1);
    }

    private void decrease(Object element) {
        this.numbers.put(element, this.numbers.get(element) - 1);
    }

    @Override
    protected Queue getQueue() {
        return new ArrayQueue();
    }
}