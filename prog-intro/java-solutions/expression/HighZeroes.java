package expression;

public class HighZeroes extends UnaryOperation {
    public HighZeroes(MyExpression element) {
        super(element, "l0");
    }
    
    protected int calculate(int value) {
        return Integer.numberOfLeadingZeros​(value);
    }

    public int priority() {
        return 3;
    }

    public boolean secondBrackets() {
        return false;
    }
}
