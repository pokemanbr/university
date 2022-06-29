package expression.generic.types;

public interface UnaryOperations<T> {
    T negate(T value);
    T count(T value);
}
