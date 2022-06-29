import java.util.Arrays;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.IndexOutOfBoundsException;

public class WordStatInput {
    public static boolean isCharacterRelevant(char c) {
        return c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION || Character.isLetter(c);
    }

    public static int getHashCode(String line) {
        final int POWER = 227, MOD = 1_000_000_007;
        int degree = 1, hash = 0;
        for (int i = 0; i < line.length(); i++) {
            hash = (hash + ((int) line.charAt(i) * degree)) % MOD;
            degree = (degree * POWER) % MOD;
        }  
        return hash;
    }

    public static void main(String args[]) {   
        BufferedReader in = null;  
        try {            
            in = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(args[0]),
                    "utf8"
                )
            );
            String[] words = new String[0];
            int amountWords = 0;
            while (true) {                
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                line = line.toLowerCase();
                int cursor = 0;
                for (int i = 0; i < line.length(); i++) {
                    if (isCharacterRelevant(line.charAt(i))) {
                        cursor = i;
                        while (i < line.length() && isCharacterRelevant(line.charAt(i))) {
                            i++;
                        }
                        String word = line.substring(cursor, i);
                        if (amountWords == words.length) {
                            words = Arrays.copyOf(words, 2 * words.length + 1);
                        }
                        words[amountWords++] = word;
                    }
                }
            }
            words = Arrays.copyOf(words, amountWords); 
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(args[1]),
                        "utf8"
                    )
                );
                int[] hashes = new int[amountWords];
                boolean[] used = new boolean[amountWords];
                for (int i = 0; i < amountWords; i++) {
                    hashes[i] = getHashCode(words[i]);
                }
                Arrays.sort(hashes);
                for (int i = 0; i < amountWords; i++) {
                    int hashLine = getHashCode(words[i]);
                    int left = -1, right = amountWords - 1;
                    while (right - left > 1) {
                        int mid = (left + right) / 2;
                        if (hashLine <= hashes[mid]) {
                            right = mid;
                        } else {
                            left = mid;
                        }
                    }
                    if (!used[right]) {
                        int count = 0;
                        for (int j = right; j < amountWords && hashLine == hashes[j]; j++) {
                            count++;
                            used[j] = true;
                        }
                        out.write(words[i] + " " + count + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Can't read file: " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Can't find the name of file: " + e.getMessage());
            } finally {
                try {    
                    out.close();
                } catch (IOException e) {
                    System.out.println("Can't read file: " + e.getMessage());
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Can't find the name of file: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Can't read file: " + e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                System.out.println("Can't read file: " + e.getMessage());
            }
        }
    }
}
