package game;

import java.util.Scanner;

public class HumanPlayer implements Player {
    private final Scanner in;

    public HumanPlayer(Scanner in) {
        this.in = in;
    }

    @Override
    public Move makeMove(Position position) {
        System.out.println();
        System.out.println("Current position");
        System.out.println(position);
        System.out.println("Enter you move for " + position.getTurn());
        while (true) {
            int row, column;
            while (!in.hasNextInt()) {
                in.next();
                System.out.println("Invalid input, write number");
            }
            row = in.nextInt();
            while (!in.hasNextInt()) {
                in.next();
                System.out.println("Invalid input, write number");
            }   
            column = in.nextInt();
            final Move move = new Move(row - 1, column - 1, position.getTurn());
            if (position.isValid(move)) {
                return move;
            }
            System.out.println("Your " + move + " is incorrect");
        }
    }
}
