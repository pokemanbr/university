package game;

import java.util.Arrays;
import java.util.Map;

public abstract class AbstractBoard implements Board {
    protected static final Map<Cell, String> CELL_TO_STRING = Map.of(
            Cell.E, ".",
            Cell.X, "X",
            Cell.O, "0"
    );

    protected int n, m, k, freeCells;
    protected Cell[][] field;
    protected Cell turn;

    public AbstractBoard(int n, int m, int k) {
        if (n <= 0 || m <= 0 || k <= 0) {
             throw new IllegalArgumentException("n, m and k should be positive");
        }
        if (k > Math.max(n, m)) {
             throw new IllegalArgumentException("k can't be more than maximum of n and m");
        }
        this.n = n;
        this.m = m;
        this.k = k;
        freeCells = n * m;
        field = new Cell[n][m];
        for (Cell[] row : field) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }

    private int length(int number) {
        return String.valueOf(number).length();
    }

    Position position = new Position() {
        @Override
        public Cell getTurn() {
            return turn;
        }

        @Override
        public boolean isValid(final Move move) {
            return 0 <= move.getRow() && move.getRow() < n
                && 0 <= move.getCol() && move.getCol() < m
                && field[move.getRow()][move.getCol()] == Cell.E
                && turn == move.getValue();
        }
    
        @Override
        public Cell getCell(int row, int column) {
            return field[row][column];
        }   

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();

            for (int spaces = 0; spaces <= length(n); spaces++) {
                sb.append(" ");
            }
            for (int c = 1; c <= m; c++) {
                sb.append(c).append(" ");
            }
            sb.append(System.lineSeparator());
    
            for (int r = 0; r < n; r++) {
        
                sb.append(r + 1).append(" ");
                int countSpaces = spacesAtTheBeginning(r);
                while (countSpaces > 0) {
                    sb.append(" ");
                    countSpaces--;
                }    
    
                for (int c = 0; c < m; c++) {
                    sb.append(CELL_TO_STRING.get(getCell(r, c)));
                    countSpaces = length(c + 1);
                    while (countSpaces > 0) {
                        sb.append(" ");
                        countSpaces--;
                    }
                }
    
                sb.append(System.lineSeparator());
            }

            sb.setLength(sb.length() - System.lineSeparator().length());
            return sb.toString();
        }
    };  
    
   
    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public GameResult makeMove(Move move) {
        if (!position.isValid(move)) {
            return GameResult.LOOSE;
        }
        freeCells--;
    
        field[move.getRow()][move.getCol()] = move.getValue();
        if (checkWin(move)) {
            return GameResult.WIN;
        }
    
        if (checkDraw()) {
            return GameResult.DRAW;
        }
    
        turn = turn == Cell.X ? Cell.O : Cell.X;
        return GameResult.UNKNOWN;
    }

    private boolean checkDraw() {
        return freeCells == 0;
    }

    protected abstract boolean checkWin(Move move);

    protected abstract int spacesAtTheBeginning(int r);

    @Override
    public String toString() {
        return position.toString();
    }

    @Override
    public void clear() {
        freeCells = n * m;
        for (Cell[] row : field) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }
}
