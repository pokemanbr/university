package expression.exceptions.errors;

import expression.exceptions.errors.CalculationException;

public class OverflowException extends CalculationException {
    public OverflowException(String message) {
        super(message);
    }
}
