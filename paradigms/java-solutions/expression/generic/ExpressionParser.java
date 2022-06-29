package expression.generic;

import expression.exceptions.BaseParser;
import expression.exceptions.StringSource;
import expression.exceptions.errors.ArgumentException;
import expression.exceptions.errors.ParenthesisException;
import expression.exceptions.errors.ParsingException;
import expression.exceptions.errors.WrongOperationException;
import expression.generic.types.Operations;

import java.util.Map;

public class ExpressionParser<T> extends BaseParser implements TripleParser<T> {
    private final Operations<T> operations;

    public ExpressionParser(Operations<T> operations) {
        this.operations = operations;
    }

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
    public MyExpression<T> parse(final String expression) throws ParsingException {
        newSource(new StringSource(expression));
        currentOperation = "";
        haveSpaces = false;
        return getExpression(MIN_PRIORITY, false);
    }

    private String currentOperation = "";

    private MyExpression<T> getExpression(final int leastPriority, boolean bracket) throws ParsingException {
        MyExpression<T> exp = getValue();
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

    private MyExpression<T> makeExpressions(final String operation, final MyExpression<T> exp1, final MyExpression<T> exp2) throws ParsingException {
        return switch (operation) {
            case ("-") -> new Subtract<>(exp1, exp2, operations);
            case ("+") -> new Add<>(exp1, exp2, operations);
            case ("*") -> new Multiply<>(exp1, exp2, operations);
            case ("/") -> new Divide<>(exp1, exp2, operations);
            case ("max") -> new Max<>(exp1, exp2, operations);
            case ("min") -> new Min<>(exp1, exp2, operations);
            default -> throw new WrongOperationException("Unexpected operation", getPosition());
        };
    }

    private boolean closed = false;

    private MyExpression<T> getValue() throws ParsingException {
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
            return new Const<>(number.toString(), operations);
        }
        if (minus) {
            return new Negate<>(getValue(), operations);
        }
        skipWhitespaces();
        if (take('(')) {
            final MyExpression<T> exp = getExpression(MIN_PRIORITY, true);
            expect(')');
            closed = true;
            return exp;
        } else if (take('c')) {
            expect("ount");
            skipWhitespaces();
            if (!haveSpaces && !test('(')) {
                throw new WrongOperationException("Can't parse unary operation", getPosition());
            }
            return new Count<>(getValue(), operations);
        } else if (between('x', 'z')) {
            final String var = Character.toString(take());
            return new Variable<>(var);
        }
        throw new ArgumentException("Expected argument", getPosition());
    }
}
