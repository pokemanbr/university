package game;

import java.util.Scanner;

public class CheatingPlayer implements Player {
    private int n, m;

    public CheatingPlayer(int n, int m) {
        this.n = n;
        this.m = m;
    }
    
    @Override
    public Move makeMove(Position position) {
        final MnkBoard board = (MnkBoard) position;
        Move first = null;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                final Move move = new Move(r, c, position.getTurn());
                if (position.isValid(move)) {
                    if (first == null) {
                        first = move;
                    } else {
                        board.makeMove(move);
                    }
                }
            }
        }
        return first;
    }
}
