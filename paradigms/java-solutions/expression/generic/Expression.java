package expression.generic;

public interface Expression<T> {
    T evaluate(T value);
}
