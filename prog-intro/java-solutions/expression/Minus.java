package expression;

public class Minus extends UnaryOperation {
    public Minus(MyExpression element) {
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
