package expression.parser;

import expression.*;

import java.util.Map;

public class ExpressionParser extends BaseParser implements Parser {
    final private Map<String, Integer> operationPriority = Map.of(
            "min", 0,
            "max", 0,
            "-", 1,
            "+", 1,
            "*", 2,
            "/", 2
    );

    private static final int MIN_PRIORITY = 0;

    @Override
    public MyExpression parse(final String expression) {
        newSource(new StringSource(expression));
        return getExpression(MIN_PRIORITY);
    }  

    private String currentOperation = "";

    private MyExpression getExpression(final int leastPriority) {
        MyExpression exp = getValue();
        skipWhitespaces();
        // :NOTE: ')'
        while (!test(')') && !test(END)) {
            if (currentOperation.isEmpty()) {
                currentOperation = getOperation();
                skipWhitespaces();
            }
            final int currentPriority = operationPriority.get(currentOperation);
            if (currentPriority < leastPriority) {
                return exp;
            } 
            final String string = currentOperation;
            currentOperation = "";
            exp = makeExpressions(string, exp, getExpression(currentPriority + 1));
            skipWhitespaces();
        }
        return exp;
    }

    private String getOperation() {
        if (take('+')) {
            return "+";
        } else if (take('-')) {
            return "-";
        } else if (take('*')) {
            return "*";
        } else if (take('/')) {
            return "/";
        } else if (take('m')) {
            // :NOTE: Идентификаторы
            if (take('i')) {
                expect('n');
                return "min";
            } else if (take('a')) {
                expect('x');
                return "max";
            }
        }
        throw new AssertionError("Unexpected operation");
    }

    private static MyExpression makeExpressions(final String operation, final MyExpression exp1, final MyExpression exp2) {
        switch (operation) {
            case "-":
                return new Subtract(exp1, exp2);
            case "+":
                return new Add(exp1, exp2);
            case ("*"):
                return new Multiply(exp1, exp2);
            case ("/"):
                return new Divide(exp1, exp2);
            case ("max"):
                return new Max(exp1, exp2);
            case ("min"):
                return new Min(exp1, exp2);
            default:   
                throw new AssertionError("Unexpected operation: " + operation);
        }
    }

    private MyExpression getValue() {
        final boolean minus = take('-');
        if (between('0', '9')) {
            final StringBuilder number = new StringBuilder();
            if (minus) {
                number.append("-");
            }
            while (between('0', '9')) {
                number.append(take());
            }
            return new Const(Integer.parseInt(number.toString()));
        }
        skipWhitespaces();
//        if (minus) {
//            return new Minus(getValue());
//        }
        if (take('(')) {
            final MyExpression exp = getExpression(MIN_PRIORITY);
            expect(')');
            return minus ? new Minus(exp) : exp;
        } else if (take('l')) {
            expect('0');
            skipWhitespaces();
            return minus ? new Minus(new HighZeroes(getValue())) : new HighZeroes(getValue());
        } else if (take('t')) {
            expect('0');
            skipWhitespaces();
            return minus ? new Minus(new LowZeroes(getValue())) : new LowZeroes(getValue());
        } else if (between('x', 'z')) {
            final String var = Character.toString(take());
            return minus ? new Minus(new Variable(var)) : new Variable(var);
        } else {
            return minus ? new Minus(getValue()) : getValue();
        }
    }
}
