package expression.exceptions;

import expression.Divide;
import expression.MyExpression;
import expression.exceptions.errors.DivisionByZeroException;
import expression.exceptions.errors.OverflowException;

public class CheckedDivide extends Divide {

    public CheckedDivide(MyExpression element1, MyExpression element2) {
        super(element1, element2);
    }

    private void checkDivisionByZero(int value) {
        if (value == 0) {
            throw new DivisionByZeroException("A division by zero has occurred");
        }
    }

    private void checkOverflow(int value1, int value2) {
        if (value1 == Integer.MIN_VALUE && value2 == -1) {
            throw new OverflowException("An overflow has occurred: " + value1 + " / (" + value2 + ")");
        }
    }

    @Override
    protected int calculate(int value1, int value2) {
        checkDivisionByZero(value2);
        checkOverflow(value1, value2);
        return value1 / value2;
    }
}
