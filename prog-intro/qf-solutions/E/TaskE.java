import java.io.*;
import java.util.*;

public class TaskE {
    static class IntList {
        public int size = 0, capacity = 1;
        private int[] list = new int[1];
    
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

    static class CheckerTaskE implements Checker {
        public boolean isNormalChar(char c) {
            return !Character.isWhitespace(c);
        }
    }

    static void dfs(int cur, int[] prev, int[] depth, IntList[] roads) {
        if (prev[cur] >= 0) {
            depth[cur] = depth[prev[cur]] + 1;
        }        
        for (int to = 0; to < roads[cur].size; to++) {
            if (roads[cur].get(to) != prev[cur]) {
                prev[roads[cur].get(to)] = cur;
                dfs(roads[cur].get(to), prev, depth, roads);
            }
        }
    }

    public static void main(String args[]) {
        try {
            CheckerTaskE check = new CheckerTaskE();
            Scanner in = new Scanner(System.in, check, "utf8");  
            try {
                int n, m;
                n = in.nextInt();
                m = in.nextInt();
                in.skipLine();
                IntList[] roads = new IntList[n];
                for (int i = 0; i < n; i++) {
                    roads[i] = new IntList();
                }
                for (int i = 0; i < n - 1; i++) {
                    int v, u;
                    v = in.nextInt();
                    u = in.nextInt();
                    in.skipLine();
                    v--;
                    u--; 
                    roads[v].add(u);
                    roads[u].add(v);
                }
                int[] c = new int[m];
                for (int i = 0; i < m; i++) {
                    c[i] = in.nextInt() - 1;
                }
                in.skipLine();
                int[] depth = new int[n];
                int[] prev = new int[n];
                prev[c[0]] = -1;
                dfs(c[0], prev, depth, roads);
                int maxCity = c[0];
                for (int i = 1; i < m; i++) {
                    if (depth[c[i]] > depth[maxCity]) {
                        maxCity = c[i];
                    }
                }
                if (depth[maxCity] % 2 == 1) {
                    System.out.println("NO");
                } else {
                    int mid = maxCity;
                    for (int i = 0; i < depth[maxCity] / 2; i++) {
                        mid = prev[mid];
                    }
                    int depthMax = depth[maxCity] / 2;
                    Arrays.fill(depth, 0);
                    Arrays.fill(prev, 0);
                    prev[mid] = -1;
                    dfs(mid, prev, depth, roads);
                    boolean ok = true;
                    for (int i = 0; i < m && ok; i++) {
                        if (depth[c[i]] != depthMax) {
                            ok = false;
                        }
                    }
                    if (ok) {
                        System.out.println("YES");
                        System.out.println(mid + 1);
                    } else {
                        System.out.println("NO");
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
