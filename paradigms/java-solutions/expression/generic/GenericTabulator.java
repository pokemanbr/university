package expression.generic;

import expression.generic.types.*;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        return createTable(typeExpression(mode), expression, x1, x2, y1, y2, z1, z2);
    }
    
    private <T> Object[][][] createTable(Operations<T> type, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        MyExpression<T> exp = new ExpressionParser<T>(type).parse(expression);

        for (int x = 0; x <= x2 - x1; x++) {
            T xNew = new Const<T>(String.valueOf(x1 + x), type).getElement();
            for (int y = 0; y <= y2 - y1; y++) {
                T yNew = new Const<T>(String.valueOf(y1 + y), type).getElement();
                for (int z = 0; z <= z2 - z1; z++) {
                    T zNew = new Const<T>(String.valueOf(z1 + z), type).getElement();

                    try {
                        table[x][y][z] = exp.evaluate(xNew, yNew, zNew);
                    } catch (Exception e) {
                        table[x][y][z] = null;
                    }
                }
            }
        }

        return table;
    }

    private Operations<?> typeExpression(String mode) {
        return switch (mode) {
            case ("i") -> new IntegerOperation(true);
            case ("d") -> new DoubleOperation();
            case ("bi") -> new BigIntegerOperation();
            case ("u") -> new IntegerOperation(false);
            case ("l") -> new LongOperation();
            case ("f") -> new FloatOperation();
            default -> null;
        };
    }
}
