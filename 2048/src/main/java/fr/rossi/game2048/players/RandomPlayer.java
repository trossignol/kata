package fr.rossi.game2048.players;

import java.io.IOException;
import java.util.Random;

import fr.rossi.game2048.Grid;
import fr.rossi.game2048.Move.Direction;

public class RandomPlayer implements Player {

    @Override
    public Direction getDirection(Grid grid) {
        return Direction.values()[new Random().nextInt(Direction.values().length)];
    }

    @Override
    public void close() throws IOException {
    }
}
