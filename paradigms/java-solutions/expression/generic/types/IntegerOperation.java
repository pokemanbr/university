package expression.generic.types;

import expression.exceptions.errors.DivisionByZeroException;
import expression.exceptions.errors.OverflowException;

public class IntegerOperation implements Operations<Integer> {
    private final boolean checker;

    public IntegerOperation(boolean checker) {
        this.checker = checker;
    }

    @Override
    public Integer subtract(Integer value1, Integer value2) {
        if (checker) {
            checkSubtractOverflow(value1, value2);
        }
        return value1 - value2;
    }

    private void checkSubtractOverflow(int value1, int value2) {
        if (value2 > 0) {
            if (value1 < Integer.MIN_VALUE + value2) {
                throw new OverflowException("An overflow has occurred: " + value1 + " - " + value2);
            }
        } else {
            if (value1 > Integer.MAX_VALUE + value2) {
                throw new OverflowException("An overflow has occurred: " + value1 + " - (" + value2 + ")");
            }
        }
    }

    @Override
    public Integer add(Integer value1, Integer value2) {
        if (checker) {
            checkAddOverflow(value1, value2);
        }
        return value1 + value2;
    }

    private void checkAddOverflow(int value1, int value2) {
        if (value1 > 0) {
            if (value2 > Integer.MAX_VALUE - value1) {
                throw new OverflowException("An overflow has occurred: " + value1 + " + " + value2);
            }
        } else {
            if (value2 < Integer.MIN_VALUE - value1) {
                throw new OverflowException("An overflow has occurred: " + value1 + " + (" + value2 + ")");
            }
        }
    }

    @Override
    public Integer multiply(Integer value1, Integer value2) {
        if (checker) {
            checkMultiplyOverflow(value1, value2);
        }
        return value1 * value2;
    }

    private void checkMultiplyOverflow(int value1, int value2) {
        if (value1 == 0 || value2 == 0) {
            return;
        }
        int maxValue = (Integer.signum(value1) == Integer.signum(value2) ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        if (value1 != -1 && value2 > 0 && value2 > maxValue / value1) {
            throw new OverflowException("An overflow has occurred: " + value1 + " * " + value2);
        }
        if (value2 < 0 && value2 < maxValue / value1) {
            throw new OverflowException("An overflow has occurred: " + value1 + " * " + value2);
        }
    }

    @Override
    public Integer divide(Integer value1, Integer value2) {
        if (checker) {
            checkDivisionByZero(value2);
            checkDivideOverflow(value1, value2);
        }
        return value1 / value2;
    }

    private void checkDivisionByZero(int value) {
        if (value == 0) {
            throw new DivisionByZeroException("A division by zero has occurred");
        }
    }

    private void checkDivideOverflow(int value1, int value2) {
        if (value1 == Integer.MIN_VALUE && value2 == -1) {
            throw new OverflowException("An overflow has occurred: " + value1 + " / (" + value2 + ")");
        }
    }

    @Override
    public Integer max(Integer value1, Integer value2) {
        return Integer.max(value1, value2);
    }

    @Override
    public Integer min(Integer value1, Integer value2) {
        return Integer.min(value1, value2);
    }

    @Override
    public Integer cnst(String number) {
        return Integer.parseInt(number);
    }

    @Override
    public Integer count(Integer value) {
        return Integer.bitCount(value);
    }

    @Override
    public Integer negate(Integer value) {
        if (checker) {
            checkNegateOverflow(value);
        }
        return -value;
    }

    private void checkNegateOverflow(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException("An overflow has occurred: -(" + value + ")");
        }
    }
}
