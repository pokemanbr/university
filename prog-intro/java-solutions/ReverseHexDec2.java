import java.util.Arrays;
import java.io.IOException;


public class ReverseHexDec2 {
    static class CheckerReverseHexDec2 implements Checker {
        public boolean isNormalChar(char c) {
            return !Character.isWhitespace(c);
        }
    }

    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(System.in, new CheckerReverseHexDec2(), "utf8");
            try {
                int[][] numbers = new int[0][];
                int countLines = 0;
                while (in.hasInput()) {
                    if (countLines == numbers.length) {
                        numbers = Arrays.copyOf(numbers, numbers.length * 2 + 1);
                    }
                    int[] row = new int[0];
                    int countNumbers = 0;
                    while (in.hasNextInLine()) {                    
                        if (countNumbers == row.length) {
                            row = Arrays.copyOf(row, row.length * 2 + 1);
                        }   
                        String number = in.next();
                        row[countNumbers++] = number.startsWith("0x") || number.startsWith("0X")
                                ? Integer.parseUnsignedInt(number.substring(2), 16)
                                : Integer.parseInt(number, 10);
                    }
                    numbers[countLines++] = Arrays.copyOf(row, countNumbers);
                }
                numbers = Arrays.copyOf(numbers, countLines);

                for (int i = numbers.length - 1; i >= 0; i--) {
                    for (int j = numbers[i].length - 1; j >= 0; j--) {
                        System.out.print("0x" + Integer.toHexString(numbers[i][j]) + " ");
                    }
                    System.out.println();
                }
            } catch (IOException e) {
                System.out.println("Can't read the Scanner: " + e.getMessage());
            } finally {
                in.close();
            }
        } catch (IOException e) {
            System.out.println("Can't read the Scanner: " + e.getMessage());
        }    
    }
}
