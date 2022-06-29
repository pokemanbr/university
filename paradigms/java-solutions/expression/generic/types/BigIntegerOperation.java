package expression.generic.types;

import java.math.BigInteger;

public class BigIntegerOperation implements Operations<BigInteger> {
    @Override
    public BigInteger subtract(BigInteger value1, BigInteger value2) {
        return value1.subtract(value2);
    }

    @Override
    public BigInteger add(BigInteger value1, BigInteger value2) {
        return value1.add(value2);
    }

    @Override
    public BigInteger multiply(BigInteger value1, BigInteger value2) {
        return value1.multiply(value2);
    }

    @Override
    public BigInteger divide(BigInteger value1, BigInteger value2) {
        return value1.divide(value2);
    }

    @Override
    public BigInteger max(BigInteger value1, BigInteger value2) {
        return value1.max(value2);
    }

    @Override
    public BigInteger min(BigInteger value1, BigInteger value2) {
        return value1.min(value2);
    }

    @Override
    public BigInteger cnst(String number) {
        return new BigInteger(number);
    }

    @Override
    public BigInteger count(BigInteger value) {
        return BigInteger.valueOf(value.bitCount());
    }

    @Override
    public BigInteger negate(BigInteger value) {
        return value.negate();
    }
}
