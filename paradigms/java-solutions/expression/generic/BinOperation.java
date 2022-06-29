package expression.generic;

import expression.generic.types.Operations;

public abstract class BinOperation<T> implements MyExpression<T> {
    final protected Operations<T> operations;
    private final MyExpression<T> element1, element2;

    public BinOperation(MyExpression<T> element1, MyExpression<T> element2, Operations<T> operations) {
        this.element1 = element1;
        this.element2 = element2;
        this.operations = operations;
    }

    abstract protected T calculate(T value1, T value2);

    @Override
    public T evaluate(T x) {
        return calculate(element1.evaluate(x), element2.evaluate(x));
    }
    @Override

    public T evaluate(T x, T y, T z) {
        return calculate(element1.evaluate(x, y, z), element2.evaluate(x, y, z));
    }
}
