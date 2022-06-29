package md2html;

import java.io.IOException;
import java.util.Map;

public class Parser {
    private String text;
    private int index;
    final private String[] marks = {"**", "*", "__", "_", "--", "```", "`"};
    final private Map<String, String> openedMarks = Map.ofEntries(
            Map.entry("*", "<em>"),
            Map.entry("_", "<em>"),
            Map.entry("**", "<strong>"),
            Map.entry("__", "<strong>"),
            Map.entry("--", "<s>"),
            Map.entry("`", "<code>"),
            Map.entry("```", "<pre>")
    );
    final private Map<String, String> closedMarks = Map.ofEntries(
            Map.entry("*", "</em>"),
            Map.entry("_", "</em>"),
            Map.entry("**", "</strong>"),
            Map.entry("__", "</strong>"),
            Map.entry("--", "</s>"),
            Map.entry("`", "</code>"),
            Map.entry("```", "</pre>")
    );
    final private Map<Character, String> specialMarks = Map.ofEntries(
            Map.entry('<', "&lt;"),
            Map.entry('>', "&gt;"),
            Map.entry('&', "&amp;")
    );

    public String parse(Scanner in) throws IOException {
        StringBuilder html = new StringBuilder();
        while (in.hasInput()) {
            StringBuilder newText = new StringBuilder();
            String line = in.nextLine();
            index = 0;
            while (!line.isBlank()) {
                newText.append(line);
                if (in.hasInput()) {
                    line = System.lineSeparator() + in.nextLine();
                } else {
                    break;
                }
            }
            text = newText.toString();
            if (!text.isEmpty()) {
                html.append(parseParagraph());
            }
        }
        return html.toString();
    }

    private String parseParagraph() {
        StringBuilder result = new StringBuilder();
        while (index < text.length() && text.charAt(index) == '#') {
            index++;
        } 
        if (index < text.length() && !Character.isWhitespace(text.charAt(index))) {
            index = 0;
        }
        int countOfHashes = index;
        if (countOfHashes == 0) {
            result.append("<p>" + parseText("", false) + "</p>");
        } else {
            result.append("<h" + countOfHashes +  ">");
            while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
                index++;
            }
            result.append(parseText("", false) + "</h" + countOfHashes + ">");
        }
        result.append(System.lineSeparator());
        return result.toString();
    }

    private boolean isMark = false;

    private String parseText(String openMark, boolean pre) {
        StringBuilder result = new StringBuilder();
        while (index < text.length()) {
            if (!pre && text.charAt(index) == '\\') {
                if (index + 1 < text.length()) {
                    index++;
                    if (text.charAt(index) == '\\') {
                        index++;
                    }
                    char c = text.charAt(index);
                    if (c != '*' && c != '_' && c != '-' && c != '`') {
                        result.append('\\');
                    }
                    result.append(c);
                } else {
                    result.append('\\');
                }
                index++;
            } else if (!openMark.isEmpty() && openMark.equals(checkForMark())) {
                result.append(closedMarks.get(openMark));
                index += openMark.length();
                isMark = true;
                return result.toString();
            } else if (!pre && !checkForMark().isEmpty()) {
                String mark = checkForMark();
                index += mark.length();
                String parsed = parseText(mark, mark.equals("```"));
                if (isMark) {
                    result.append(openedMarks.get(mark));
                } else {
                    result.append(mark);
                }
                result.append(parsed);
                isMark = false;
            } else {
                if (!pre) {
                    result.append(specialMarks.getOrDefault(text.charAt(index), Character.toString(text.charAt(index))));
                } else {
                    result.append(text.charAt(index));
                }
                index++;
            }
        }
        return result.toString(); 
    }   

    private String checkForMark() {
        for (String mark : marks) {
            if (index + mark.length() <= text.length() && check(mark, index)) {
                return mark;
            }
        }
        return "";
    }

    private boolean check(String mark, int index) {
        for (int i = index; i < index + mark.length(); i++) {
            if (mark.charAt(i - index) != text.charAt(i)) {
                return false;
            }
        } 
        return true;
    }
}
