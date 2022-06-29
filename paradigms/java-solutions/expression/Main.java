package expression;

import java.util.Scanner;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int x = in.nextInt();
        var vvv = new Add(new Subtract(new Multiply(new Variable("x"), new Variable("x")),
                new Multiply(new Const(2), new Variable("x"))), new Const(1));
        int answer = vvv.evaluate(x);
        System.out.println(vvv.toMiniString());
        System.out.println(answer);
    }
}
