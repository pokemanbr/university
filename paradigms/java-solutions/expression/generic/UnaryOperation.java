package expression.generic;

import expression.generic.types.Operations;

public abstract class UnaryOperation<T> implements MyExpression<T> {
    protected final Operations<T> operations;
    private final MyExpression<T> element;

    public UnaryOperation(MyExpression<T> element, Operations<T> operations) {
        this.element = element;
        this.operations = operations;
    }

    abstract protected T calculate(T value);

    @Override
    public T evaluate(T x) {
        return calculate(element.evaluate(x));
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return calculate(element.evaluate(x, y, z));
    }
}
