package md2html;

import java.io.*;

public class Md2Html {
    public static final Checker CHECKER = new Checker() {
        @Override
        public boolean isNormalChar(final char c) {
            return !Character.isWhitespace(c);
        }
    };
    
    public static void main(String[] args) {
        
        String html = new String();

        try {            
            final Scanner in = new Scanner(new FileInputStream(args[0]), CHECKER, "utf8");
            try {
                Parser mdToHtml = new Parser();
                html = mdToHtml.parse(in);
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
            out.write(html);
        } catch (final IOException e) {
            System.out.println("Can't write to file: " + e.getMessage());
        } catch (final IndexOutOfBoundsException e) {
            System.out.println("Can't find the name of file: " + e.getMessage());
        } 
    }
}
