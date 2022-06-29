package expression.generic.types;

public class FloatOperation implements Operations<Float> {
    @Override
    public Float subtract(Float value1, Float value2) {
        return value1 - value2;
    }

    @Override
    public Float add(Float value1, Float value2) {
        return value1 + value2;
    }

    @Override
    public Float multiply(Float value1, Float value2) {
        return value1 * value2;
    }

    @Override
    public Float divide(Float value1, Float value2) {
        return value1 / value2;
    }

    @Override
    public Float max(Float value1, Float value2) {
        return Float.max(value1, value2);
    }

    @Override
    public Float min(Float value1, Float value2) {
        return Float.min(value1, value2);
    }

    @Override
    public Float cnst(String number) {
        return Float.parseFloat(number);
    }

    @Override
    public Float count(Float value) {
        return (float) Integer.bitCount(Float.floatToIntBits(value));
    }

    @Override
    public Float negate(Float value) {
        return -value;
    }
}
