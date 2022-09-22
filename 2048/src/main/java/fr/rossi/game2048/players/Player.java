package fr.rossi.game2048.players;

import fr.rossi.game2048.Grid;
import fr.rossi.game2048.Move.Direction;

public interface Player {
    Direction getDirection(Grid grid);
}
