package expression.exceptions.errors;

public class WrongOperationException extends ParsingException {
    public WrongOperationException(String message, int errorOffset) {
        super(message, errorOffset);
    }
}
