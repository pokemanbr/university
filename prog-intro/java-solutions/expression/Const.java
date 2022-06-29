package expression;

import java.math.BigInteger;

public class Const implements MyExpression {
    private final boolean big;
    private final int value;
    private final BigInteger bigValue;

    public Const(int value) {
        this.value = value;
        big = false;
        bigValue = BigInteger.ZERO;
    }
    
    public Const(BigInteger value) {
        this.value = 0;
        big = true;
        bigValue = value;
    }

    @Override 
    public int evaluate(int value) {
        return this.value;
    }

    @Override 
    public BigInteger evaluate(BigInteger value) {
        return bigValue;
    }

    @Override 
    public int evaluate(int value1, int value2, int value3) {
        return this.value;
    }

    @Override
    public String toString() {
        if (big) {
            return bigValue.toString();
        }
        return Integer.toString(value);
    }
    
    @Override
    public int priority() {
        return 4;
    }

    @Override
    public boolean secondBrackets() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Const) {
            Const check = (Const) o;
            if (big) {
                return bigValue.equals(check.bigValue);
            }
            return this.value == check.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (big) {
            return bigValue.intValue();
        }
        return value;
    }

}
