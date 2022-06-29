package game;

import java.util.Arrays;
import java.util.Map;

public class HexBoard extends AbstractBoard {

    private int length(int number) {
        return String.valueOf(number).length();
    }

    protected int spacesAtTheBeginning(int r) {
        return r - length(r + 1) + length(n);
    }

    public HexBoard(int n, int m, int k) {
        super(n, m, k);
    }

    protected boolean checkWin(Move move) {
        final int[][] steps = {{0, 1}, {1, 0}, {-1, 1}};

        for (int step = 0; step < 3; step++) {
            int count = 1;

            int r = move.getRow() + steps[step][0], c = move.getCol() + steps[step][1];
            while (0 <= r && r < n && 0 <= c && c < m && super.position.getCell(r, c) == move.getValue() && count < k) {
                count++;
                r += steps[step][0];
                c += steps[step][1];
            }

            r = move.getRow() - steps[step][0]; c = move.getCol() - steps[step][1];
            while (0 <= r && r < n && 0 <= c && c < m && super.position.getCell(r, c) == move.getValue() && count < k) {
                count++;
                r -= steps[step][0];
                c -= steps[step][1];
            }

            if (count == k) {
                return true;
            }
        }

        return false;
    }
}
