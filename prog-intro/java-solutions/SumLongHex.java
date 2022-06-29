public class SumLongHex {
    public static void main(String[] args) {
        long answer = 0;
        for (int i = 0; i < args.length; i++) {
            args[i] += ' ';
            for (int cursor = 0; cursor < args[i].length(); cursor++) {
		        if (!Character.isWhitespace(args[i].charAt(cursor))) {
                    int first = cursor;
		            while (cursor < args[i].length() && !Character.isWhitespace(args[i].charAt(cursor))) {
		                cursor++;
		            }
                    String number = args[i].substring(first, cursor);
                    if (number.startsWith("0x") || number.startsWith("0X")) {
                        answer += Long.parseUnsignedLong(number.substring(2, number.length()), 16);
                    } else {
                        answer += Long.parseLong(number, 10);
                    }
                }
            }
        }    
        System.out.println(answer);
    }
}
