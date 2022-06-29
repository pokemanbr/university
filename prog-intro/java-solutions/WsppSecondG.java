import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public class WsppSecondG {
    public static final Checker CHECKER = new Checker() {
        @Override
        public boolean isNormalChar(final char c) {
            return c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION || Character.isLetter(c);
        }
    };

    public static void main(final String[] args) {
        final Map<String, IntList> words = new LinkedHashMap<>();

        try {
            final Scanner in = new Scanner(new FileInputStream(args[0]), CHECKER, "utf8");
            try {
                int pos = 1;
                int line = 1;
                while (in.hasInput()) {                
                    while (in.hasNextInLine()) {
                        final String word = in.next().toLowerCase();
                        words.putIfAbsent(word, new IntList(new int[]{0, line, 0}));
                        final IntList enters = words.get(word);

                        enters.set(0, enters.get(0) + 1);

                        if (enters.get(1) != line) {
                            enters.set(1, line);
                            enters.set(2, 0);
                        }

                        if (enters.get(2) == 1) {
                            enters.add(pos);
                        }            
                        enters.set(2, (enters.get(2) + 1) % 2);          
 
                        pos++;
                    }
                    line++;
                }
            } catch (final IOException e) {
                System.out.println("Can't read file: " + e.getMessage());
            } finally {
                in.close();
            }
        } catch (final IOException e) {
            System.out.println("Can't read file: " + e.getMessage());
        } catch (final IndexOutOfBoundsException e) {
            System.out.println("Can't find the name of file: " + e.getMessage());
        }

        try (final BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(args[1]),
                        "utf8"
                )
        )) {
            for (final Entry<String, IntList> entry : words.entrySet()) {
                final IntList enters = entry.getValue();
                out.write(entry.getKey() + " " + enters.get(0));
                if (enters.size > 3) {
                    out.write(" " + enters.toString(3, enters.size));
                }
                out.newLine();
            }
        } catch (final IOException e) {
            System.out.println("Can't write to file: " + e.getMessage());
        }
    }
}
