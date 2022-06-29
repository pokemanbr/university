package expression;

public class Abs extends UnaryOperation {
    public Abs(MyExpression element) {
        super(element, "abs");
    }

    protected int calculate(int value) {
        if (value >= 0) {
            return value;
        }
        else {
            return -value;
        }
    }

    public int priority() {
        return 3;
    }

    public boolean secondBrackets() {
        return false;
    }
}
