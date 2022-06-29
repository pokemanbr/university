package expression.generic;

import expression.generic.types.Operations;

public class Negate<T> extends UnaryOperation<T> {
    public Negate(MyExpression<T> element, Operations<T> operations) {
        super(element, operations);
    }

    @Override
    protected T calculate(T value) {
        return super.operations.negate(value);
    }
}
