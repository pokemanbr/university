package expression.generic;

public class Variable<T> implements MyExpression<T> {
    private final String var;

    public Variable(String var) {
        this.var = var;
    }

    @Override
    public T evaluate(T value) {
        return value;
    }

    @Override
    public T evaluate(T value1, T value2, T value3) {
        if (var.equals("x")) {
            return value1;
        } else if (var.equals("y")) {
            return value2;
        } else {
            return value3;
        }
    }
}
