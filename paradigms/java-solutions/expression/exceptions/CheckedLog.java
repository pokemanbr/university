package expression.exceptions;

import expression.Log;
import expression.MyExpression;
import expression.exceptions.errors.LogException;

public class CheckedLog extends Log {
    public CheckedLog(MyExpression element1, MyExpression element2) {
        super(element1, element2);
    }

    private void checkBadCases(int value1, int value2) {
        if (value1 <= 0) {
            throw new LogException("Exponent must be positive");
        }
        if (value2 <= 1) {
            throw new LogException("Basis must be greater than 1");
        }
    }

    @Override
    protected int calculate(int value1, int value2) {
        checkBadCases(value1, value2);
        int answer = 0;
        while (value1 >= value2) {
            value1 /= value2;
            answer++;
        }
        return answer;
    }
}
