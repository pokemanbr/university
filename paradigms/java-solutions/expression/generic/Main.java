package expression.generic;

public class Main {
    public static void main(String[] args) {
        String mode = args[0];
        switch (mode) {
            case ("-i") -> mode = "i";
            case ("-d") -> mode = "d";
            case ("-bi") -> mode = "bi";
            default -> mode = null;
        }
        String expression = args[1];
        int x1 = -2, x2 = 2, y1 = -2, y2 = 2, z1 = -2, z2 = 2;
        GenericTabulator fun = new GenericTabulator();
        try {
            Object[][][] result = fun.tabulate(mode, expression, x1, x2, y1, y2, z1, z2);
            for (int x = 0; x <= x2 - x1; x++) {
                for (int y = 0; y <= y2 - y1; y++) {
                    for (int z = 0; z <= z2 - z1; z++) {
                        System.out.println("x: " + (x1 + x) + ", y: " + (y1 + y) + ", z: " + (z1 + z) + ". " + expression + " = " + result[x][y][z]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
