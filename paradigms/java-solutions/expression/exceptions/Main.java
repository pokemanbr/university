package expression.exceptions;

import expression.*;
import expression.exceptions.errors.ParsingException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ParsingException {
        Scanner in = new Scanner(System.in);
        ExpressionParser vvv = new ExpressionParser();
        MyExpression ttt = vvv.parse("abs k");
        System.out.println(ttt.toMiniString());
    }   
}
