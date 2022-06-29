package expression.exceptions.errors;

public class ArgumentException extends ParsingException {
    public ArgumentException(String message, int errorOffset) {
        super(message, errorOffset);
    }
}
