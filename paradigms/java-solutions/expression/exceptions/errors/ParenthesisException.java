package expression.exceptions.errors;

public class ParenthesisException extends ParsingException {
    public ParenthesisException(String message, int errorOffset) {
        super(message, errorOffset);
    }
}
