package expression.exceptions;

import expression.Negate;
import expression.MyExpression;
import expression.exceptions.errors.OverflowException;

public class CheckedNegate extends Negate {

    public CheckedNegate(MyExpression element) {
        super(element);
    }

    private void checkOverflow(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException("An overflow has occurred: -(" + value + ")");
        }
    }

    @Override
    protected int calculate(int value) {
        checkOverflow(value);
        return -value;
    }
}
