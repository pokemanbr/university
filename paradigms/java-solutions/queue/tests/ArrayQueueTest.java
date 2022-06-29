package queue.tests;

import queue.ArrayQueue;

public class ArrayQueueTest {
    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();
        
        //add 10 elements (0..9) to the end
        fillToEnd(queue);

        //get first element without and with deleting
        dumpFromBegin(queue);

        //checking empty after functions "dequeue"
        checkEmpty(queue, "dequeue");

        //add 10 elements (0..9) to the begin
        fillToBegin(queue);

        //get last element without and with deleting
        dumpFromEnd(queue);

        //checking empty after functions "remove"
        checkEmpty(queue, "remove");

        //fill queue
        fillToBegin(queue);

        //clear queue
        queue.clear();

        //checking empty after function clear
        checkEmpty(queue, "clear");

        //try to enqueue element "null"
        try {
            queue.enqueue(null);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to push element "null"
        try {
            queue.push(null);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to remove when queue is empty
        try {
            Object element = queue.remove();
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to get element when queue is empty
        try {
            Object element = queue.element();
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to get peek when queue is empty
        try {
            Object element = queue.peek();
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }
    }

    private static void fillToEnd(ArrayQueue queue) {
        for (int i = 0; i < 10; i++) {
            //add element i to the end
            queue.enqueue(i);
        }
    }

    private static void dumpFromBegin(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            int sizeBefore = queue.size();

            //get first element without deleting
            Object firstElement1 = queue.element();

            //get first element with deleting
            Object firstElement2 = queue.dequeue();

            int sizeAfter = queue.size();

            System.out.println(sizeBefore + " " + firstElement1 + " " + firstElement2);

            if (firstElement1 != firstElement2) {
                System.out.println("Functions element and dequeue works incorrect");
            }

            if (sizeBefore - 1 != sizeAfter) {
                System.out.println("Function dequeue deletes element incorrect");
            }
        }
    }

    private static void fillToBegin(ArrayQueue queue) {
        for (int i = 0; i < 10; i++) {
            //add element i to the beginning
            queue.push(i);
        }
    }

    private static void dumpFromEnd(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            int sizeBefore = queue.size();

            //get last element without deleting
            Object firstElement1 = queue.peek();

            //get last element with deleting
            Object firstElement2 = queue.remove();

            int sizeAfter = queue.size();

            System.out.println(sizeBefore + " " + firstElement1 + " " + firstElement2);

            if (firstElement1 != firstElement2) {
                System.out.println("Functions peek and remove works incorrect");
            }

            if (sizeBefore - 1 != sizeAfter) {
                System.out.println("Function remove deletes element incorrect");
            }
        }
    }

    private static void checkEmpty(ArrayQueue queue, String operation) {
        if (!queue.isEmpty()) {
            System.out.println("Queue should be empty after deleting all elements using " + operation);
        } else if (queue.size() != 0) {
            System.out.println("Size should be equal zero when queue is empty");
        }
    }
}
