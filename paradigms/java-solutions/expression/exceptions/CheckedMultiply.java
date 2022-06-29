package expression.exceptions;

import expression.Multiply;
import expression.MyExpression;
import expression.exceptions.errors.OverflowException;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(MyExpression element1, MyExpression element2) {
        super(element1, element2);
    }

    public static void checkOverflow(int value1, int value2) {
        if (value1 == 0 || value2 == 0) {
            return;
        }
        int maxValue = (Integer.signum(value1) == Integer.signum(value2) ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        if (value1 != -1 && value2 > 0 && value2 > maxValue / value1) {
            throw new OverflowException("An overflow has occurred: " + value1 + " * " + value2);
        }
        if (value2 < 0 && value2 < maxValue / value1) {
            throw new OverflowException("An overflow has occurred: " + value1 + " * " + value2);
        }
    }

    @Override
    protected int calculate(int value1, int value2) {
        checkOverflow(value1, value2);
        return value1 * value2;
    }
}
