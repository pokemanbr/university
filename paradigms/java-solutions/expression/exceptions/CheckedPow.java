package expression.exceptions;

import expression.MyExpression;
import expression.Pow;
import expression.exceptions.CheckedMultiply;
import expression.exceptions.errors.OverflowException;
import expression.exceptions.errors.PowException;

public class CheckedPow extends Pow {
    public CheckedPow(MyExpression element1, MyExpression element2) {
        super(element1, element2);
    }

    private void checkBadCases(int value1, int value2) {
        if (value1 == 0 && value2 == 0) {
            throw new PowException("Basis and degree can't be equal to zero");
        }
        if (value2 < 0) {
            throw new PowException("Degree can't be negative");
        }
    }

    @Override
    protected int calculate(int value1, int value2) {
        checkBadCases(value1, value2);
        int answer = 1, num = value1, deg = value2;
        try {
            while (deg > 0) {
                if (deg % 2 == 1) {
                    CheckedMultiply.checkOverflow(answer, num);
                    answer *= num;
                    deg--;
                }
                if (deg > 1) {
                    CheckedMultiply.checkOverflow(num, num);
                    num *= num;
                }
                deg /= 2;
            }
            return answer;
        } catch (OverflowException e) {
            throw new OverflowException("An overflow has occurred: " + value1 + " ** " + value2);
        }
    }
}
