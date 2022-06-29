package expression.generic;

import expression.generic.types.Operations;

public class Multiply<T> extends BinOperation<T> {
    public Multiply(MyExpression<T> element1, MyExpression<T> element2, Operations<T> operations) {
        super(element1, element2, operations);
    }

    @Override
    protected T calculate(T value1, T value2) {
        return super.operations.multiply(value1, value2);
    }
}
