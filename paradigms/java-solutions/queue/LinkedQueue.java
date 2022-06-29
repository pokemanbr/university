package queue;

import java.util.Objects;

public class LinkedQueue extends AbstractQueue {
    private Node front, back;

    @Override
    protected void enqueueImpl(Object element) {
        if (Objects.isNull(this.front)) {
            this.front = this.back = new Node(Objects.requireNonNull(element));
        } else {
            this.back.next = new Node(Objects.requireNonNull(element));
            this.back = this.back.getNext();
        }
    }

    @Override
    protected Object elementImpl() {
        return this.front.element;
    }

    @Override
    protected Object dequeueImpl() {
        Object result = this.front.element;
        this.front = this.front.getNext();
        return result;
    }

    @Override
    protected void clearImpl() {
        this.front = this.back = null;
    }

    @Override
    protected Queue getQueue() {
        return new LinkedQueue();
    }

    protected static class Node {
        private final Object element;
        private Node next;

        public Node(Object element) {
            this.element = element;
            this.next = null;
        }

        public Node getNext() {
            return this.next;
        }
    }
}
