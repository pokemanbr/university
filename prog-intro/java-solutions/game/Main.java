package game;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final int n = 3, m = 3, k = 3;
        try {
            final int result = new TwoPlayerGame(
                    new HexBoard(n, m, k),
                    new RandomPlayer(n, m),
                    new HumanPlayer(new Scanner(System.in))
            ).play(true);
            switch (result) {
                case 1:
                    System.out.println("First player won");
                    break;
                case 2:
                    System.out.println("Second player won");
                    break;
                case 0:
                    System.out.println("Draw");
                    break;
                default:
                    throw new AssertionError("Unknown result " + result);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }

        Player[] players = new Player[3];
        players[0] = new RandomPlayer(n, m);
        players[1] = new RandomPlayer(n, m);
        players[2] = new RandomPlayer(n, m);
        try {
            int[] result = new Tournament(players, new MnkBoard(n, m, k)).play(true);
            System.out.println();
            System.out.println("Tournament results: ");        
            for (int i = 0; i < players.length; i++) {
                System.out.format("Player %d: %d points", i + 1, result[i]);
                System.out.println();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }
}
