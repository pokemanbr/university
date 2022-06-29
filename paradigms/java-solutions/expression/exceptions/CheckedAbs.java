package expression.exceptions;

import expression.Abs;
import expression.MyExpression;
import expression.exceptions.errors.OverflowException;

public class CheckedAbs extends Abs {

    public CheckedAbs(MyExpression element) {
        super(element);
    }

    private void checkOverflow(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException("An overflow has occurred: |" + (value) + "|");
        }
    }

    @Override
    protected int calculate(int value) {
        checkOverflow(value);
        return value < 0 ? -value : value;
    }
}
