import java.util.Arrays;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.IndexOutOfBoundsException;

class CheckerWordStatWords implements Checker {
    public boolean isNormalChar(char c) {
        return c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION || Character.isLetter(c);
    }
}

public class WordStatWords {
    public static void main(String args[]) {   
        try {
            CheckerWordStatWords check = new CheckerWordStatWords();
            Scanner input = new Scanner(new FileInputStream(args[0]), check, "utf8");
            try { 
                String[] words = new String[0];
                int countWords = 0;
                while (input.hasNext()) {               
                    if (words.length == countWords) {
                        words = Arrays.copyOf(words, words.length * 2 + 1);
                    }         
                    words[countWords++] = input.next().toLowerCase();
                }
                words = Arrays.copyOf(words, countWords); 
                Arrays.sort(words);
                try {
                    BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(
                            new FileOutputStream(args[1]),
                            "utf8"
                        )
                    );
                    try {
                        int left = 0;
                        while (left < countWords) {
                            int right = left;
                            while (right < countWords && words[left].equals(words[right])) {
                                right++;
                            }
                            out.write(words[left] + " " + (right - left));
                            out.newLine();
                            left = right;
                        }
                    } catch (IOException e) {
                        System.out.println("Can't write to the file: " + e.getMessage());
                    } finally {
                        out.close();
                    }
                } catch (IOException e) {
                    System.out.println("Can't write to the file: " + e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("Can't read the file: " + e.getMessage());
            } finally {
                input.close();
            }
        } catch (IOException e) {
            System.out.println("Can't read the file: " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Can't find the name of the file: " + e.getMessage());
        }
    }
}
