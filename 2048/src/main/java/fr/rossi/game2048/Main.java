package fr.rossi.game2048;

import java.io.IOException;

import fr.rossi.game2048.players.KeyboardPlayer;

public class Main {
    public static void main(String[] args) throws IOException {
        try (var player = new KeyboardPlayer()) {
            var grid = new Grid(4);

            while (true) {
                System.out.println(grid);
                var direction = player.getDirection(grid);

                // TODO Check if apply as made a move
                grid = new Move(direction, grid).apply().setValue();

                // TODO Handle game over
            }
        }
    }
}
