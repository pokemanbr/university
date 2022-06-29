public class Sum {
    public static void main(String[] args) {
        int answer = 0;
        for (int i = 0; i < args.length; i++) {
            int cursor = 0;
            args[i] += ' ';
            while (cursor < args[i].length() && Character.isWhitespace(args[i].charAt(cursor)) == true) {
                cursor++;
            }
            for (int j = cursor; j < args[i].length(); j++) {
		        if (Character.isWhitespace(args[i].charAt(j)) == true) {
                    answer += Integer.parseInt(args[i].substring(cursor, j));
		            while (j + 1 < args[i].length() && Character.isWhitespace(args[i].charAt(j + 1)) == true) {
                        j++;
                    }
                    cursor = j + 1;
                }
            }
        }    
        System.out.println(answer);
    }
}
