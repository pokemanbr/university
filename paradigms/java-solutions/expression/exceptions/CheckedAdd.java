package expression.exceptions;

import expression.Add;
import expression.BinOperation;
import expression.MyExpression;
import expression.exceptions.errors.OverflowException;


public class CheckedAdd extends Add {

    public CheckedAdd(MyExpression element1, MyExpression element2) {
        super(element1, element2);
    }

    private void checkOverflow(int value1, int value2) {
        if (value1 > 0) {
            if (value2 > Integer.MAX_VALUE - value1) {
                throw new OverflowException("An overflow has occurred: " + value1 + " + " + value2);
            }
        } else {
            if (value2 < Integer.MIN_VALUE - value1) {
                throw new OverflowException("An overflow has occurred: " + value1 + " + (" + value2 + ")");
            }
        }
    }

    @Override
    protected int calculate(int value1, int value2) {
        checkOverflow(value1, value2);
        return value1 + value2;
    }
}
