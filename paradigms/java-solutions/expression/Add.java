package expression;

import java.math.BigInteger;

public class Add extends BinOperation {
    public Add(MyExpression element1, MyExpression element2) {
        super(element1, element2, "+");
    }

    protected int calculate(int value1, int value2) {
        return value1 + value2;
    }

    public int priority() {
        return 1;
    }

    public boolean secondBrackets() {
        return false;
    }
}
