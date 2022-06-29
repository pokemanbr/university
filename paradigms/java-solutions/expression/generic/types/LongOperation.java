package expression.generic.types;

public class LongOperation implements Operations<Long> {

    @Override
    public Long subtract(Long value1, Long value2) {
        return value1 - value2;
    }

    @Override
    public Long add(Long value1, Long value2) {
        return value1 + value2;
    }

    @Override
    public Long multiply(Long value1, Long value2) {
        return value1 * value2;
    }

    @Override
    public Long divide(Long value1, Long value2) {
        return value1 / value2;
    }

    @Override
    public Long max(Long value1, Long value2) {
        return Long.max(value1, value2);
    }

    @Override
    public Long min(Long value1, Long value2) {
        return Long.min(value1, value2);
    }

    @Override
    public Long cnst(String number) {
        return Long.parseLong(number);
    }

    @Override
    public Long count(Long value) {
        return (long) Long.bitCount(value);
    }

    @Override
    public Long negate(Long value) {
        return -value;
    }
}
