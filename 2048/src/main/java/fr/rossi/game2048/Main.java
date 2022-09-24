package fr.rossi.game2048;

import java.io.IOException;

import fr.rossi.game2048.players.ai.AIPlayer;

public class Main {
    public static void main(String[] args) throws IOException {
        try (var player = new AIPlayer()) {
            var grid = new Grid(4);

            while (true) {
                System.out.println(grid);
                var direction = player.getDirection(grid);
                System.out.println(direction);

                var newGrid = new Move(direction, grid).apply();
                if (newGrid.isPresent()) {
                    grid = newGrid.get().setValue();
                }

                if (Move.isOver(grid)) {
                    System.out.println("Game over");
                    return;
                }
            }
        }
    }
}
