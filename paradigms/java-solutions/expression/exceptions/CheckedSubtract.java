package expression.exceptions;

import expression.Subtract;
import expression.MyExpression;
import expression.exceptions.errors.OverflowException;

public class CheckedSubtract extends Subtract {

    public CheckedSubtract(MyExpression element1, MyExpression element2) {
        super(element1, element2);
    }

    private void checkOverflow(int value1, int value2) {
        if (value2 > 0) {
            if (value1 < Integer.MIN_VALUE + value2) {
                throw new OverflowException("An overflow has occurred: " + value1 + " - " + value2);
            }
        } else {
            if (value1 > Integer.MAX_VALUE + value2) {
                throw new OverflowException("An overflow has occurred: " + value1 + " - (" + value2 + ")");
            }
        }
    }

    @Override
    protected int calculate(int value1, int value2) {
        checkOverflow(value1, value2);
        return value1 - value2;
    }
}
