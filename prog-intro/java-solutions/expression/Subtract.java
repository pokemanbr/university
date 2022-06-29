package expression;

import java.math.BigInteger;

public class Subtract extends BinOperation {
    public Subtract(MyExpression element1, MyExpression element2) {
        super(element1, element2, "-");
    }

    protected int calculate(int value1, int value2) {
        return value1 - value2;
    }

    protected BigInteger calculate(BigInteger value1, BigInteger value2) {
        return value1.subtract(value2);
    }

    public int priority() {
        return 1;
    }

    public boolean secondBrackets() {
        return true;
    }
}
