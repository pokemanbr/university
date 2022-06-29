import java.io.*;
import java.util.*;
import java.util.HashMap;

public class TaskM {
    static class Scanner {
        private boolean haveSeparator = false;
        private Reader reader;
        private int index, read;
        private char[] block = new char[256];
        private Checker checker;
    
        public Scanner(String in, Checker check) throws IOException {
            reader = new StringReader(in);
            checker = check;
            nextBlock();
        }     
        
        public Scanner(InputStream in, Checker check, String encoding) throws IOException {
            reader = new InputStreamReader(in, encoding);
            checker = check;
            nextBlock();
        }
    
        private void nextBlock() throws IOException {
            read = reader.read(block);
            index = 0;
        }    
    
        private void incIndex() throws IOException {
            if (haveSeparator) {
                haveSeparator = false;
            } else {
                index++;
                if (index == read) {
                    nextBlock();
                }
            }
        }
    
        public boolean hasInput() {
            return read != -1 || haveSeparator;
        }
    
        private boolean isSeparatorNow() throws IOException {
            if (haveSeparator || block[index] == '\n') {
                return true;
            }
            if (block[index] == '\r') {
                incIndex();
                if (!hasInput() || block[index] != '\n') {
                    haveSeparator = true;
                }
                return true;
            }
            return false;
        }
    
        private boolean isBadCharExceptSeparator(char c) throws IOException {
            return !checker.isNormalChar(c) && !isSeparatorNow();
        }
    
        private void skipBadChars() throws IOException {
            while (hasInput() && isBadCharExceptSeparator(block[index])) {
                incIndex();
            }
        }
    
        private boolean isBadChar() {
            return haveSeparator || !checker.isNormalChar(block[index]);
        }
        
        public boolean hasNext() throws IOException {
            while (hasInput()) {
                if (!isBadChar()) {
                    break;
                }
                incIndex();
            }
            return hasInput() && !isBadChar();
        }
    
        public boolean hasNextInLine() throws IOException {
            skipBadChars();
            if (!hasInput() || isSeparatorNow()) {
                if (hasInput()) {
                    incIndex();
                }
                return false;
            } else {
                return true;
            }
        }
    
        public void skipLine() throws IOException {
            while (hasInput()) {
                if (isSeparatorNow()) {
                    incIndex();
                    break;
                }
                incIndex();
            }
        }
    
        public String nextLine() throws IOException {
            StringBuilder result = new StringBuilder();
            while (hasInput()) {
                if (isSeparatorNow()) {
                    incIndex();
                    break;
                }
                result.append(block[index]);
                incIndex();
            }
            return result.toString();
        }
    
        public String next() throws IOException {
            StringBuilder result = new StringBuilder();
            skipBadChars();
            while (hasInput() && !isBadChar()) {
                result.append(block[index]);
                incIndex();
            }
            return result.toString();
        }    
        
        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    
        public void close() throws IOException {
            reader.close();
        }
    }

    static interface Checker {
        boolean isNormalChar(char c);
    }

    static class CheckerTaskM implements Checker {
        public boolean isNormalChar(char c) {
            return !Character.isWhitespace(c);
        }
    }

    public static void main(String args[]) {
        try {
            CheckerTaskM check = new CheckerTaskM();
            Scanner in = new Scanner(System.in, check, "utf8");  
            try {
                int t;
                t = in.nextInt();
                in.skipLine();
                for (int numberOfTest = 0; numberOfTest < t; numberOfTest++) {
                    int n;
                    n = in.nextInt();
                    in.skipLine();
                    int[] a = new int[n];   
                    Map<Integer, Integer> NumbersOfAk = new HashMap<Integer, Integer>();
                    for (int i = 0; i < n; i++) {
                        a[i] = in.nextInt();
                        NumbersOfAk.putIfAbsent(a[i], 0);
                        if (i >= 1) {
                            NumbersOfAk.compute(a[i], (key, value) -> value + 1);
                        }
                    }       
                    in.skipLine();  
                    int triplets = 0;
                    for (int j = 1; j < n; j++) {
                        NumbersOfAk.compute(a[j], (key, value) -> value - 1);
                        for (int i = 0; i < j; i++) {
                            int sum = 2 * a[j] - a[i];
                            triplets += NumbersOfAk.getOrDefault(sum, 0);
                        }
                     }
                    System.out.println(triplets);
                }
            } catch (IOException e) {
                System.out.println("Can't read Scanner: " + e.getMessage());
            } finally {
                in.close();
            }
        } catch (IOException e) {
            System.out.println("Can't read Scanner: " + e.getMessage());
        } 
    }
}
