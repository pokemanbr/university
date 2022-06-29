import java.util.Arrays;

public class IntList {
    public int size = 0, capacity = 1;
    private int[] list;

    public IntList() {
        list = new int[1];
    }

    public IntList(int[] array) {
        list = new int[1];
        for (int value : array) {
            add(value);
        }
    }

    private void checkCapacity() {
        if (size == capacity) {
            capacity *= 2;
            list = Arrays.copyOf(list, capacity);
        }
    }

    public void set(int index, int value) {
        if (index < size) {
            list[index] = value;
        }
    }

    public void add(int value) {
        checkCapacity();
        list[size++] = value;
    }

    public int get(int index) {
        return list[index];
    }     

    public String toString(int begin, int end) {
        StringBuilder line = new StringBuilder();
        for (int i = begin; i < end; i++) {
            line.append(Integer.toString(get(i)));
            if (i + 1 != end) {
                line.append(" ");
            }
        }
        return line.toString();
    }
}
