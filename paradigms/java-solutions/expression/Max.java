package expression;

import java.math.BigInteger;

public class Max extends BinOperation {
    public Max(MyExpression element1, MyExpression element2) {
        super(element1, element2, "max");
    }

    protected int calculate(int value1, int value2) {
        return Integer.max(value1, value2);
    }

    public int priority() {
        return 0;
    }

    public boolean secondBrackets() {
        return true;
    }
}
