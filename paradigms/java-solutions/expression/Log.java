package expression;

import java.math.BigInteger;

public class Log extends BinOperation {
    public Log(MyExpression element1, MyExpression element2) {
        super(element1, element2, "//");
    }

    protected int calculate(int value1, int value2) {
        int answer = 0;
        while (value1 >= value2) {
            value1 /= value2;
            answer++;
        }
        return answer;
    }

    public int priority() {
        return 3;
    }

    public boolean secondBrackets() {
        return true;
    }
}
