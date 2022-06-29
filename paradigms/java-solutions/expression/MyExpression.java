package expression;

public interface MyExpression extends Expression, TripleExpression {
    int priority();
    boolean secondBrackets();
}
