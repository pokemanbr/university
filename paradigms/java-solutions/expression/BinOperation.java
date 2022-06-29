package expression;

import java.math.BigInteger;
import java.util.Objects;


public abstract class BinOperation implements MyExpression {
    private final String operation;
    private final MyExpression element1, element2;

    public BinOperation(MyExpression element1, MyExpression element2, String operation) {
        this.element1 = element1;
        this.element2 = element2;
        this.operation = operation;
    }

    abstract protected int calculate(int value1, int value2);

    @Override 
    public int evaluate(int value) {
        return calculate(element1.evaluate(value), element2.evaluate(value));
    }

    @Override 
    public int evaluate(int value1, int value2, int value3) {
        return calculate(element1.evaluate(value1, value2, value3), element2.evaluate(value1, value2, value3));
    }

    @Override
    public String toString() {
        return "(" + element1.toString() + " " + operation + " " + element2.toString() + ")";
    }

    @Override
    abstract public int priority();

    @Override
    abstract public boolean secondBrackets();

    @Override
    public String toMiniString() {
        StringBuilder result = new StringBuilder();
        if (element1.priority() < priority()) {
            result.append("(").append(element1.toMiniString()).append(")");
        } else {
            result.append(element1.toMiniString());
        }
        result.append(" ").append(operation).append(" ");
        if (element2.priority() < priority() || element2.priority() == priority() &&
                ((secondBrackets() && (element2 instanceof BinOperation) && priority() != 0 || priority() >= 2 && element2.secondBrackets())
                        || (priority() == 0 && (secondBrackets() ^ element2.secondBrackets())))
        ) {
            result.append("(").append(element2.toMiniString()).append(")");
        } else {
            result.append(element2.toMiniString());
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BinOperation) {
            BinOperation check = (BinOperation) o;
            return operation.equals(check.operation) && element1.equals(check.element1) && element2.equals(check.element2);
        }
        return false;
    }

    private boolean hasHash = false;
    private int hash;
    
    @Override
    public int hashCode() {
        if (!hasHash) {
            hash = Objects.hash(element1, element2, operation);
        }   
        hasHash = true;
        return hash;
    }
}
