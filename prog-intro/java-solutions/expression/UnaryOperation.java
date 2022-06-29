package expression;

import java.math.BigInteger;
import java.util.Objects;

public abstract class UnaryOperation implements MyExpression {
    private final String operation;
    private final MyExpression element;

    public UnaryOperation(MyExpression element, String operation) {
        this.element = element;
        this.operation = operation;
    }

    abstract protected int calculate(int value);

    @Override 
    public int evaluate(int value) {
        return calculate(element.evaluate(value));
    }

    @Override 
    public BigInteger evaluate(BigInteger value) {
        return element.evaluate(value);
    }

    @Override 
    public int evaluate(int value1, int value2, int value3) {
        return calculate(element.evaluate(value1, value2, value3));
    }

    @Override
    public String toString() {
        return operation + "(" + element.toString() + ")";
    }

    @Override
    public String toMiniString() {
        if (element instanceof BinOperation) {
            return operation + "(" + element.toMiniString() + ")";
        } else {
            return operation + " " + element.toMiniString();
        }
    }

    @Override
    abstract public int priority();

    @Override
    abstract public boolean secondBrackets();

    @Override
    public boolean equals(Object o) {
        if (o instanceof UnaryOperation) {
            UnaryOperation check = (UnaryOperation) o;
            return operation.equals(check.element) && element.equals(check.element);
        }
        return false;
    }

    private boolean hasHash = false;
    private int hash;
    
    @Override
    public int hashCode() {
        if (!hasHash) {
            hash = Objects.hash(element);
        }   
        hasHash = true;
        return hash;
    }
}
