package expression.generic;

public interface TripleExpression<T> {
    T evaluate(T value1, T value2, T value3);
}
