package fr.rossi.game2048.players;

import java.io.Closeable;

import fr.rossi.game2048.Grid;
import fr.rossi.game2048.Move.Direction;

public interface Player extends Closeable {
    Direction getDirection(Grid grid);
}
