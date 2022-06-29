package expression.generic;

import expression.generic.types.Operations;

public class Count<T> extends UnaryOperation<T> {
    public Count(MyExpression<T> element, Operations<T> operations) {
        super(element, operations);
    }

    @Override
    protected T calculate(T value) {
        return super.operations.count(value);
    }
}
