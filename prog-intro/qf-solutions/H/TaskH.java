import java.io.*;
import java.util.*;

public class TaskH {
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

    static class CheckerTaskH implements Checker {
        public boolean isNormalChar(char c) {
            return !Character.isWhitespace(c);
        }
    }

    public static void main(String args[]) {
        try {
            CheckerTaskH check = new CheckerTaskH();
            Scanner in = new Scanner(System.in, check, "utf8");  
            try {
                int n;
                n = in.nextInt();
                in.skipLine();
                int[] a = new int[n];
                int maxA = 0, sumA = 0;
                for (int i = 0; i < n; i++) {
                    a[i] = in.nextInt();
                    maxA = Math.max(maxA, a[i]);
                    sumA += a[i];
                }                
                in.skipLine();
                int[] blockOfQuerie = new int[sumA];
                int[] startOfBlock = new int[n];     
                int querie = 0;     
                for (int i = 0; i < n; i++) {
                    startOfBlock[i] = querie;
                    for (int j = 0; j < a[i]; j++) {
                        blockOfQuerie[querie++] = i;
                    }
                }
                int q;
                q = in.nextInt();
                in.skipLine();
                int[] transactions = new int[sumA + 1];
                for (int count = 0; count < q; count++) {
                    int t;
                    t = in.nextInt();
                    if (transactions[t] == 0) {
                        int newT = 0;
                        for (int number = 1; true; number++) {
                            newT += t;
                            if (newT >= blockOfQuerie.length) {
                                transactions[t] = number;
                                break;
                            } else if (blockOfQuerie[newT - t] == blockOfQuerie[newT]) {
                                transactions[t] = -1;
                                break;
                            }
                            newT = startOfBlock[blockOfQuerie[newT]];
                        }
                    }
                    if (transactions[t] > 0) {
                        System.out.println(transactions[t]);
                    } else {
                        System.out.println("Impossible");
                    }
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
