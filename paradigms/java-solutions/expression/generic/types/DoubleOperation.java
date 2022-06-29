package expression.generic.types;

public class DoubleOperation implements Operations<Double>{
    @Override
    public Double subtract(Double value1, Double value2) {
        return value1 - value2;
    }

    @Override
    public Double add(Double value1, Double value2) {
        return value1 + value2;
    }

    @Override
    public Double multiply(Double value1, Double value2) {
        return value1 * value2;
    }

    @Override
    public Double divide(Double value1, Double value2) {
        return value1 / value2;
    }

    @Override
    public Double max(Double value1, Double value2) {
        return Double.max(value1, value2);
    }

    @Override
    public Double min(Double value1, Double value2) {
        return Double.min(value1, value2);
    }

    @Override
    public Double count(Double value) {
        return (double) Long.bitCount(Double.doubleToLongBits(value));
    }

    @Override
    public Double cnst(String number) {
        return Double.parseDouble(number);
    }

    @Override
    public Double negate(Double value) {
        return -value;
    }
}
