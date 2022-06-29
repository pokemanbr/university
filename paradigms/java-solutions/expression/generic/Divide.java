package expression.generic;

import expression.generic.types.Operations;

public class Divide<T> extends BinOperation<T> {
    public Divide(MyExpression<T> element1, MyExpression<T> element2, Operations<T> operations) {
        super(element1, element2, operations);
    }

    @Override
    protected T calculate(T value1, T value2) {
        return super.operations.divide(value1, value2);
    }
}
