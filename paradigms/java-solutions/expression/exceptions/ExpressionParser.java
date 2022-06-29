package expression.exceptions;

import expression.*;
import expression.exceptions.errors.ArgumentException;
import expression.exceptions.errors.ParenthesisException;
import expression.exceptions.errors.ParsingException;
import expression.exceptions.errors.WrongOperationException;

import java.util.Map;

public class ExpressionParser extends BaseParser implements TripleParser {
    final private Map<String, Integer> operationPriority = Map.of(
            "min", 0,
            "max", 0,
            "-", 1,
            "+", 1,
            "*", 2,
            "/", 2,
            "**", 3,
            "//", 3
    );

    private static final int MIN_PRIORITY = 0;

    @Override
    public MyExpression parse(final String expression) throws ParsingException {
        newSource(new StringSource(expression));
        currentOperation = "";
        haveSpaces = false;
        return getExpression(MIN_PRIORITY, false);
    }

    private String currentOperation = "";

    private MyExpression getExpression(final int leastPriority, boolean bracket) throws ParsingException {
        MyExpression exp = getValue();
        while (!test(')') && !test(END)) {
            if (currentOperation.isEmpty()) {
                currentOperation = getOperation(bracket);
            }
            if (currentOperation.isEmpty()) {
                return exp;
            }
            final int currentPriority = operationPriority.get(currentOperation);
            if (currentPriority < leastPriority) {
                return exp;
            }
            final String string = currentOperation;
            currentOperation = "";
            exp = makeExpressions(string, exp, getExpression(currentPriority + 1, bracket));
        }
        if (test(')') && !bracket) {
            throw new ParenthesisException("Expected opening parenthesis for closing parenthesis", getPosition());
        }
        if (test(END) && bracket) {
            throw new ParenthesisException("Expected closing parenthesis for opening parenthesis", getPosition());
        }
        if (!currentOperation.isEmpty()) {
            throw new ArgumentException("Expected argument after operation", getPosition());
        }
        return exp;
    }

    private String getOperation(boolean bracket) throws ParsingException {
        skipWhitespaces();
        if (take('+')) {
            return "+";
        } else if (take('-')) {
            return "-";
        } else if (take('*')) {
            if (take('*')) {
                return "**";
            }
            return "*";
        } else if (take('/')) {
            if (take('/')) {
                return "//";
            }
            return "/";
        } else if ((haveSpaces || closed) && take('m')) {
            if (take('i')) {
                expect('n');
                skipWhitespaces();
                if (!haveSpaces && !test('-') && !test('(')) {
                    throw new WrongOperationException("Can't parse binary operation", getPosition());
                }
                return "min";
            } else if (take('a')) {
                expect('x');
                skipWhitespaces();
                if (!haveSpaces && !test('-') && !test('(')) {
                    throw new WrongOperationException("Can't parse binary operation", getPosition());
                }
                return "max";
            }
        } else if (take('a')) {
            expect("bs");
            return "abs";
        }
        skipWhitespaces();
        if (bracket && (test(')') || test(END))) {
            return "";
        }
        throw new WrongOperationException("Can't parse binary operation", getPosition());
    }

    private MyExpression makeExpressions(final String operation, final MyExpression exp1, final MyExpression exp2) throws ParsingException {
        return switch (operation) {
            case ("-") -> new CheckedSubtract(exp1, exp2);
            case ("+") -> new CheckedAdd(exp1, exp2);
            case ("*") -> new CheckedMultiply(exp1, exp2);
            case ("/") -> new CheckedDivide(exp1, exp2);
            case ("**") -> new CheckedPow(exp1, exp2);
            case ("//") -> new CheckedLog(exp1, exp2);
            case ("max") -> new Max(exp1, exp2);
            case ("min") -> new Min(exp1, exp2);
            default -> throw new WrongOperationException("Unexpected operation", getPosition());
        };
    }

    private boolean closed = false;

    private MyExpression getValue() throws ParsingException {
        skipWhitespaces();
        closed = false;
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
        if (minus) {
            return new CheckedNegate(getValue());
        }
        skipWhitespaces();
        if (take('(')) {
            final MyExpression exp = getExpression(MIN_PRIORITY, true);
            expect(')');
            closed = true;
            return exp;
        } else if (take('a')) {
            expect("bs");
            skipWhitespaces();
            if (!haveSpaces && !test('(')) {
                throw new WrongOperationException("Can't parse unary operation", getPosition());
            }
            return new CheckedAbs(getValue());
        } else if (between('x', 'z')) {
            final String var = Character.toString(take());
            return new Variable(var);
        }
        throw new ArgumentException("Expected argument", getPosition());
    }
}
