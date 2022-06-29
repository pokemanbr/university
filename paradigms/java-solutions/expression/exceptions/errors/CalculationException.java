package expression.exceptions.errors;

public class CalculationException extends RuntimeException {
    CalculationException(String message) {
        super(message);
    }
}
