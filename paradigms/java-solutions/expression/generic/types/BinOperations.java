package expression.generic.types;

public interface BinOperations<T> {
    T subtract(T value1, T value2);
    T add(T value1, T value2);
    T multiply(T value1, T value2);
    T divide(T value1, T value2);
    T max(T value1, T value2);
    T min(T value1, T value2);
}
