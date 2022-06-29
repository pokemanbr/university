package expression.generic;

import expression.generic.types.Operations;

public class Const<T> implements MyExpression<T> {
    private final String element;
    private final Operations<T> operations;

    public Const(String element, Operations<T> operations) {
        this.element = element;
        this.operations = operations;
    }

    private T calculate(String value) {
        return operations.cnst(value);
    }

    @Override
    public T evaluate(T x) {
        return calculate(element);
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return calculate(element);
    }

    public T getElement() {
        return calculate(element);
    }
}
