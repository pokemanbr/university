import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.LinkedHashMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

class OurChecker implements Checker {
    public boolean isNormalChar(char c) {
        return c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION || Character.isLetter(c);
    }
}

public class Wspp {
    public static void main(String args[]) {   
        try {
            OurChecker check = new OurChecker();
            Scanner in = new Scanner(new FileInputStream(args[0]), check, "utf8");  
            try {
                Map<String, IntList> words = new LinkedHashMap<String, IntList>();
                int index = 1;
                while (in.hasNext()) {                
                    String word = in.next();
                    word = word.toLowerCase();
                    IntList enters;
                    if (words.get(word) == null) {                            
                        enters = new IntList();
                        words.put(word, enters);
                    } else {
                        enters = words.get(word);
                    }
                    enters.add(index++);
                }
                try {
                    BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(
                            new FileOutputStream(args[1]),
                            "utf8"
                        )
                    );
                    try {                
                        Iterator<Entry<String, IntList>> it = words.entrySet().iterator();
                        while (it.hasNext()) {
                            Entry<String, IntList> entry = it.next();
                            IntList enters = entry.getValue();
                            out.write(entry.getKey() + " " + enters.size + " ");
                            for (int i = 0; i < enters.size; i++) {
                                out.write(Integer.toString(enters.get(i)));
                                if (i + 1 != enters.size) {
                                    out.write(" ");
                                }
                            }
                            out.newLine();
                        }
                    } catch (IOException e) {
                        System.out.println("Can't read file: " + e.getMessage());
                    } finally {
                        out.close();
                    }
                } catch (IOException e) {
                   System.out.println("Can't read file: " + e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("Can't read file: " + e.getMessage());
            } finally {
                in.close();
            }
        } catch (IOException e) {
            System.out.println("Can't read file: " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Can't find the name of file: " + e.getMessage());
        }
    }
}
