package expression;

import java.math.BigInteger;

public class Variable implements MyExpression {
    private final String var;

    public Variable(String var) {
        this.var = var;
    }

    @Override 
    public int evaluate(int value) {
        return value;
    }
    
    @Override 
    public BigInteger evaluate(BigInteger value) {
        return value;
    }

    @Override 
    public int evaluate(int value1, int value2, int value3) {
        if (var.equals("x")) {        
            return value1;
        } else if (var.equals("y")) {
            return value2;
        } else {
            return value3;
        }
    }

    @Override
    public String toString() {
        return var;
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
        if (o instanceof Variable) {
            Variable check = (Variable) o;
            return var.equals(check.var);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return var.hashCode();
    }
}
