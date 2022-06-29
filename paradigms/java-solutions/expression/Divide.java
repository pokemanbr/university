package expression;

import java.math.BigInteger;

public class Divide extends BinOperation {
    public Divide(MyExpression element1, MyExpression element2) {
        super(element1, element2, "/");
    }

    protected int calculate(int value1, int value2) {
        return value1 / value2;
    }

    public int priority() {
        return 2;
    }

    public boolean secondBrackets() {
        return true;
    }
}
