import java.io.*;
import java.util.*;

public class TaskI {
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

    static class CheckerTaskI implements Checker {
        public boolean isNormalChar(char c) {
            return !Character.isWhitespace(c);
        }
    }

    public static void main(String args[]) {
        try {
            CheckerTaskI check = new CheckerTaskI();
            Scanner in = new Scanner(System.in, check, "utf8");  
            try {
                int n;
                n = in.nextInt();
                in.skipLine();
                int[] x = new int[n];
                int[] y = new int[n];
                int[] h = new int[n];
                for (int i = 0; i < n; i++) {
                    x[i] = in.nextInt();
                    y[i] = in.nextInt();
                    h[i] = in.nextInt();
                    in.skipLine();
                }
                int xLeft, xRight, yLeft, yRight; 
                xLeft = x[0] - h[0];
                xRight = x[0] + h[0];
                yLeft = y[0] - h[0];
                yRight = y[0] + h[0];
                for (int i = 1; i < n; i++) {
                    xLeft = Math.min(xLeft, x[i] - h[i]);
                    xRight = Math.max(xRight, x[i] + h[i]);
                    yLeft = Math.min(yLeft, y[i] - h[i]);
                    yRight = Math.max(yRight, y[i] + h[i]);    
                }
                int answerX, answerY, answerH;
                answerX = (xLeft + xRight) / 2;
                answerY = (yLeft + yRight) / 2;
                answerH = (Math.max(xRight - xLeft, yRight - yLeft)) / 2 + (Math.max(xRight - xLeft, yRight - yLeft)) % 2;
                System.out.println(answerX + " " + answerY + " " + answerH);
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
