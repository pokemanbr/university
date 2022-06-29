package queue.tests;

import queue.ArrayQueueModule;

public class ArrayQueueModuleTest {
    public static void main(String[] args) {
        //add 10 elements (0..9) to the end
        fillToEnd();

        //get first element without and with deleting
        dumpFromBegin();

        //checking empty after functions "dequeue"
        checkEmpty("dequeue");

        //add 10 elements (0..9) to the begin
        fillToBegin();

        //get last element without and with deleting
        dumpFromEnd();

        //checking empty after functions "remove"
        checkEmpty("remove");

        //fill queue
        fillToBegin();

        //clear queue
        ArrayQueueModule.clear();

        //checking empty after function clear
        checkEmpty("clear");

        //try to enqueue element "null"
        try {
            ArrayQueueModule.enqueue(null);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to push element "null"
        try {
            ArrayQueueModule.push(null);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to remove when queue is empty
        try {
            Object element = ArrayQueueModule.remove();
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to get element when queue is empty
        try {
            Object element = ArrayQueueModule.element();
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to get peek when queue is empty
        try {
            Object element = ArrayQueueModule.peek();
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }
    }

    private static void fillToEnd() {
        for (int i = 0; i < 10; i++) {
            //add element i to the end
            ArrayQueueModule.enqueue(i);
        }
    }

    private static void dumpFromBegin() {
        while (!ArrayQueueModule.isEmpty()) {
            int sizeBefore = ArrayQueueModule.size();

            //get first element without deleting
            Object firstElement1 = ArrayQueueModule.element();

            //get first element with deleting
            Object firstElement2 = ArrayQueueModule.dequeue();

            int sizeAfter = ArrayQueueModule.size();

            System.out.println(sizeBefore + " " + firstElement1 + " " + firstElement2);

            if (firstElement1 != firstElement2) {
                System.out.println("Functions element and dequeue works incorrect");
            }

            if (sizeBefore - 1 != sizeAfter) {
                System.out.println("Function dequeue deletes element incorrect");
            }
        }
    }

    private static void fillToBegin() {
        for (int i = 0; i < 10; i++) {
            //add element i to the beginning
            ArrayQueueModule.push(i);
        }
    }

    private static void dumpFromEnd() {
        while (!ArrayQueueModule.isEmpty()) {
            int sizeBefore = ArrayQueueModule.size();

            //get last element without deleting
            Object firstElement1 = ArrayQueueModule.peek();

            //get last element with deleting
            Object firstElement2 = ArrayQueueModule.remove();

            int sizeAfter = ArrayQueueModule.size();

            System.out.println(sizeBefore + " " + firstElement1 + " " + firstElement2);

            if (firstElement1 != firstElement2) {
                System.out.println("Functions peek and remove works incorrect");
            }

            if (sizeBefore - 1 != sizeAfter) {
                System.out.println("Function remove deletes element incorrect");
            }
        }
    }

    private static void checkEmpty(String operation) {
        if (!ArrayQueueModule.isEmpty()) {
            System.out.println("Queue should be empty after deleting all elements using " + operation);
        } else if (ArrayQueueModule.size() != 0) {
            System.out.println("Size should be equal zero when queue is empty");
        }
    }
}
