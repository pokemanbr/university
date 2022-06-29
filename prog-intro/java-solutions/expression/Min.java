package expression;

import java.math.BigInteger;

public class Min extends BinOperation {
    public Min(MyExpression element1, MyExpression element2) {
        super(element1, element2, "min");
    }

    protected int calculate(int value1, int value2) {
        return Integer.min(value1, value2);
    }

    protected BigInteger calculate(BigInteger value1, BigInteger value2) {
        return value1.min(value2);
    }

    public int priority() {
        return 0;
    }

    public boolean secondBrackets() {
        return false;
    }
}
