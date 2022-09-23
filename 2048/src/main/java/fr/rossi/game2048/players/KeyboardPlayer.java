package fr.rossi.game2048.players;

import java.io.IOException;
import java.util.Scanner;

import fr.rossi.game2048.Grid;
import fr.rossi.game2048.Move.Direction;

public class KeyboardPlayer implements Player {
    private final Scanner keyboard;

    public KeyboardPlayer() {
        this.keyboard = new Scanner(System.in);
    }

    public Direction getDirection(Grid grid) {
        System.out.println("Enter command:");
        String input;
        while (true) {
            input = keyboard.nextLine();
            var direction = switch (input) {
                case "p" -> null;
                case "z" -> Direction.UP;
                case "w" -> Direction.DOWN;
                case "q" -> Direction.LEFT;
                case "s" -> Direction.RIGHT;
                default -> null;
            };
            if (direction != null)
                return direction;
        }
    }

    @Override
    public void close() throws IOException {
        this.keyboard.close();
    }

}
