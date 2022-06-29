import java.io.*;

public class Reverse {
    public static final Checker CHECKER = new Checker() {
        @Override
        public boolean isNormalChar(final char c) {
            return !Character.isWhitespace(c);
        }
    };

    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in, CHECKER, "utf8");
            try {
                int amountLines = 0;
                int[][] listOfNumbers = new int[1_000_000][];
                int[] numbersNow = new int[1_000_000];
                while (input.hasInput()) {
                    int count = 0;
                    while (input.hasNextInLine()) {
                        numbersNow[count] = input.nextInt();
                        count++;
                    }
                    int[] numbers = new int[count];
                    for (int i = 0; i < count; i++) {
                        numbers[i] = numbersNow[i];
                    }
                    listOfNumbers[amountLines] = numbers;
                    amountLines++;
                }
                for (int i = amountLines - 1; i >= 0; i--) {
                    for (int j = listOfNumbers[i].length - 1; j >= 0; j--) {
                        System.out.print(listOfNumbers[i][j] + " ");
                    }
                    System.out.println();
                }
            } catch (IOException e) {
                System.out.println("Can't read the Scanner: " + e.getMessage());
            } finally {
                input.close();
            }
        } catch (IOException e) {
            System.out.println("Can't read the Scanner: " + e.getMessage());
        }
    }
}
