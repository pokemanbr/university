package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private final Random random = new Random();
    private int n, m;

    public RandomPlayer(int n, int m) {
        this.n = n;
        this.m = m;
    }

    @Override
    public Move makeMove(Position position) {
        final Move move = new Move(
                random.nextInt(n),
                random.nextInt(m),
                position.getTurn()
        );
        return move;
    }
}
