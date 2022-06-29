package expression;

public class Negate extends UnaryOperation {
    public Negate(MyExpression element) {
        super(element, "-");
    }
    
    protected int calculate(int value) {
        return -value;
    }

    public int priority() {
        return 3;
    }

    public boolean secondBrackets() {
        return false;
    }
}
