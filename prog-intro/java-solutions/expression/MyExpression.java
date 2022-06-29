package expression;

public interface MyExpression extends Expression, TripleExpression, BigIntegerExpression {
    int priority();
    boolean secondBrackets();
}
