package expression.exceptions.errors;

public class DivisionByZeroException extends CalculationException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
