package expression;

public class LowZeroes extends UnaryOperation {
    public LowZeroes(MyExpression element) {
        super(element, "t0");
    }
    
    protected int calculate(int value) {
        return Integer.numberOfTrailingZeros​(value);
    }

    public int priority() {
        return 3;
    }

    public boolean secondBrackets() {
        return false;
    }
}
