package queue.tests;

import queue.ArrayQueueADT;

public class ArrayQueueADTTest {
    public static void main(String[] args) {
        ArrayQueueADT queue = new ArrayQueueADT();

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
        ArrayQueueADT.clear(queue);

        //checking empty after function clear
        checkEmpty(queue, "clear");

        //try to enqueue element "null"
        try {
            ArrayQueueADT.enqueue(queue, null);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to push element "null"
        try {
            ArrayQueueADT.push(queue, null);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to remove when queue is empty
        try {
            Object element = ArrayQueueADT.remove(queue);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to get element when queue is empty
        try {
            Object element = ArrayQueueADT.element(queue);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        //try to get peek when queue is empty
        try {
            Object element = ArrayQueueADT.peek(queue);
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }
    }

    private static void fillToEnd(ArrayQueueADT queue) {
        for (int i = 0; i < 10; i++) {
            //add element i to the end
            ArrayQueueADT.enqueue(queue, i);
        }
    }

    private static void dumpFromBegin(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            int sizeBefore = ArrayQueueADT.size(queue);

            //get first element without deleting
            Object firstElement1 = ArrayQueueADT.element(queue);

            //get first element with deleting
            Object firstElement2 = ArrayQueueADT.dequeue(queue);

            int sizeAfter = ArrayQueueADT.size(queue);

            System.out.println(sizeBefore + " " + firstElement1 + " " + firstElement2);

            if (firstElement1 != firstElement2) {
                System.out.println("Functions element and dequeue works incorrect");
            }

            if (sizeBefore - 1 != sizeAfter) {
                System.out.println("Function dequeue deletes element incorrect");
            }
        }
    }

    private static void fillToBegin(ArrayQueueADT queue) {
        for (int i = 0; i < 10; i++) {
            //add element i to the beginning
            ArrayQueueADT.push(queue, i);
        }
    }

    private static void dumpFromEnd(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            int sizeBefore = ArrayQueueADT.size(queue);

            //get last element without deleting
            Object firstElement1 = ArrayQueueADT.peek(queue);

            //get last element with deleting
            Object firstElement2 = ArrayQueueADT.remove(queue);

            int sizeAfter = ArrayQueueADT.size(queue);

            System.out.println(sizeBefore + " " + firstElement1 + " " + firstElement2);

            if (firstElement1 != firstElement2) {
                System.out.println("Functions peek and remove works incorrect");
            }

            if (sizeBefore - 1 != sizeAfter) {
                System.out.println("Function remove deletes element incorrect");
            }
        }
    }

    private static void checkEmpty(ArrayQueueADT queue, String operation) {
        if (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println("Queue should be empty after deleting all elements using " + operation);
        } else if (ArrayQueueADT.size(queue) != 0) {
            System.out.println("Size should be equal zero when queue is empty");
        }
    }
}
