package game;

import java.util.Arrays;
import java.util.Map;

public class MnkBoard extends AbstractBoard {

    private int length(int number) {
        return String.valueOf(number).length();
    }    

    protected int spacesAtTheBeginning(int r) {
        return length(n) - length(r + 1);
    }

    public MnkBoard(int n, int m, int k) {
        super(n, m, k);
    }

    @Override
    protected boolean checkWin(Move move) {
        final int[][] steps = {{1, 0}, {0, 1}, {1, 1}, {-1, 1}};

        for (int step = 0; step < 4; step++) {
            int count = 1;

            int r = move.getRow() + steps[step][0], c = move.getCol() + steps[step][1];
            while (0 <= r && r < n && 0 <= c && c < m && position.getCell(r, c) == move.getValue() && count < k) {
                count++;
                r += steps[step][0];
                c += steps[step][1];
            }

            r = move.getRow() - steps[step][0]; c = move.getCol() - steps[step][1];
            while (0 <= r && r < n && 0 <= c && c < m && position.getCell(r, c) == move.getValue() && count < k) {
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
