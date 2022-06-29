package expression.generic;

public interface TripleParser<T> {
    TripleExpression<T> parse(String expression) throws Exception;
}
