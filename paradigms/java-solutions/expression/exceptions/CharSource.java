package expression.exceptions;

public interface CharSource {
    boolean hasNext();
    char next();
    int getPosition();
    IllegalArgumentException error(final String message);
}
