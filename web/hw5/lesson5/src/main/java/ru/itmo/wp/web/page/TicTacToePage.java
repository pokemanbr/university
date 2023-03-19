package ru.itmo.wp.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    private void action(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("state") == null) {
            request.getSession().setAttribute("state", new State(3));
        }
        view.put("state", request.getSession().getAttribute("state"));
    }

    private void onMove(HttpServletRequest request, Map<String, Object> view) {
        State board = (State) request.getSession().getAttribute("state");
        Map<String, String[]> map = request.getParameterMap();
        for (String key : map.keySet()) {
            if (key.startsWith("cell")) {
                int row = Character.getNumericValue(key.charAt(5));
                int col = Character.getNumericValue(key.charAt(6));
                if (board.checkCell(row, col)) {
                    board.makeMove(row, col);
                }
                break;
            }
        }
        request.getSession().setAttribute("state", board);
        view.put("state", board);
    }

    private void newGame(HttpServletRequest request, Map<String, Object> view) {
        request.getSession().removeAttribute("state");
        action(request, view);
    }

    public static class State {
        private static final String RUNNING = "RUNNING";
        private static final String DRAW = "DRAW";
        private static final String WON_O = "WON_O";
        private static final String WON_X = "WON_X";

        private final Character[][] cells;

        private boolean crossesMove;
        private final int size;
        private String phase;
        private int countOccupied = 0;

        public State(int size) {
            this.cells = new Character[size][size];
            this.size = size;
            this.crossesMove = true;
            this.phase = RUNNING;
        }

        private void makeMove(int row, int col) {
            if (phase.equals(RUNNING)) {
                cells[row][col] = popPlayer();
                checkSituation(row, col);
            }
        }

        private char popPlayer() {
            char player = (crossesMove ? 'X' : 'O');
            crossesMove ^= true;
            return player;
        }

        private void checkSituation(int row, int col) {
            countOccupied++;

            if (checkWin(row, col)) {
                phase = (crossesMove ? WON_O : WON_X);
                return;
            }

            if (countOccupied == size * size) {
                phase = DRAW;
            }
        }

        private boolean checkWin(int row, int col) {
            final int[][] steps = {{1, 0}, {0, 1}, {1, 1}, {-1, 1}};

            for (int step = 0; step < 4; step++) {
                int count = 1;

                int r = row + steps[step][0], c = col + steps[step][1];
                while (0 <= r && r < size && 0 <= c && c < size && cells[r][c] == cells[row][col]) {
                    count++;
                    r += steps[step][0];
                    c += steps[step][1];
                }

                r = row - steps[step][0]; c = col - steps[step][1];
                while (0 <= r && r < size && 0 <= c && c < size && cells[r][c] == cells[row][col]) {
                    count++;
                    r -= steps[step][0];
                    c -= steps[step][1];
                }

                if (count == size) {
                    return true;
                }
            }

            return false;
        }

        public boolean checkCell(int row, int col) {
            return 0 <= row && 0 <= col && row < size && col < size && cells[row][col] == null;
        }

        public Character[][] getCells() {
            return cells;
        }

        public boolean isCrossesMove() {
            return crossesMove;
        }

        public int getSize() {
            return size;
        }

        public String getPhase() {
            return phase;
        }
    }
}
