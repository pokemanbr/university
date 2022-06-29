package expression;

import java.math.BigInteger;

public class Pow extends BinOperation {
    public Pow(MyExpression element1, MyExpression element2) {
        super(element1, element2, "**");
    }

    protected int calculate(int value1, int value2) {
        int answer = 1, num = value1, deg = value2;
        while (deg > 0) {
            if (deg % 2 == 1) {
                answer *= num;
                deg--;
            }
            num *= num;
            deg /= 2;
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
