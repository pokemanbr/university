package expression.generic.types;

import expression.generic.UnaryOperation;

public interface Operations<T> extends BinOperations<T>, UnaryOperations<T> {
    T cnst(String number);
}
