package game;

import java.util.Scanner;
import java.util.Arrays;

public class Tournament {

    private final Player[] players;
    private final Board board;

    public Tournament(Player[] players, Board board) {
        this.board = board;
        this.players = players;    
    } 

    public int[] play(boolean log) {
        int[] result = new int[players.length];
        Arrays.fill(result, 0);   

        for (int i = 0; i < players.length; i++) {
            for (int j = i + 1; j < players.length; j++) {


                int player1 = i, player2 = j;
                for (int games = 0; games < 2; games++) {
                    if (log) {
                        System.out.println();
                        System.out.format("Game: %d vs %d", player1 + 1, player2 + 1);
                        System.out.println();
                    }
                    final int resultOfGame = new TwoPlayerGame(
                            board,
                            players[player1],
                            players[player2]
                    ).play(log);
                    switch (resultOfGame) {
                        case 1:
                            result[player1] += 3;
                            break;
                        case 2:
                            result[player2] += 3;
                            break;
                        case 0:
                            result[player1] += 1;
                            result[player2] += 1;
                            break;
                        default:
                            throw new AssertionError("Unknown result " + result);
                    }

                    player1 = j;
                    player2 = i;
                    board.clear();
                }   
            }
        }
        return result;
    }
}
