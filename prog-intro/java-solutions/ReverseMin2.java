import java.util.Scanner;
import java.util.Arrays;

public class ReverseMin2 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int amountLines = 0, maxRow = 0;
        int[][] listOfNumbers = new int[0][];
        while (input.hasNextLine()) {
            String line = input.nextLine();
            Scanner inputLine = new Scanner(line);
            int amountNumbers = 0;
            int[] numbers = new int[0];
            while (inputLine.hasNextInt()) {
                if (amountNumbers == numbers.length) {
                    numbers = Arrays.copyOf(numbers, 2 * numbers.length + 1);
                }
                numbers[amountNumbers++] = inputLine.nextInt();
            }
            maxRow = Integer.max(maxRow, amountNumbers);
            if (amountLines == listOfNumbers.length) {
                listOfNumbers = Arrays.copyOf(listOfNumbers, 2 * listOfNumbers.length + 1);
            }
            listOfNumbers[amountLines++] = Arrays.copyOf(numbers, amountNumbers);
        }
        listOfNumbers = Arrays.copyOf(listOfNumbers, amountLines);
        int[][] minMatrix = new int[amountLines][];
        int[] minimumOfColumn = new int[maxRow];
        Arrays.fill(minimumOfColumn, Integer.MAX_VALUE);
        for (int i = 0; i < listOfNumbers.length; i++) {
            minMatrix[i] = new int[listOfNumbers[i].length];
            for (int j = 0; j < listOfNumbers[i].length; j++) {
                minimumOfColumn[j] = Integer.min(minimumOfColumn[j], listOfNumbers[i][j]);
                if (j == 0) {
                    minMatrix[i][j] = minimumOfColumn[j];
                } else {
                    minMatrix[i][j] = Integer.min(minMatrix[i][j - 1], minimumOfColumn[j]);
                }
            }
        }
        for (int i = 0; i < minMatrix.length; i++) {
            for (int j = 0; j < minMatrix[i].length; j++) {
                System.out.print(minMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
