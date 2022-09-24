package fr.rossi.game2048.players.ai;

import static java.util.Arrays.stream;

import java.util.Arrays;
import java.util.Objects;

import fr.rossi.game2048.Grid;

final class GridScorer {
    private GridScorer() {
    }

    static double scoreGrid(Grid grid) {
        // TODO Evaluate empty position ?

        var empty = grid.getEmpty().size();

        var maxValue = empty == 0 ? 0
                : stream(grid.getData())
                        .flatMap(Arrays::stream)
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue).max().orElse(0);

        var score = stream(grid.getData())
                .flatMap(Arrays::stream)
                .map(value -> value == null ? maxValue : value)
                .mapToInt(Integer::intValue)
                .mapToDouble(value -> Math.pow(value, 2.0))
                .sum();

        return score;
    }
}
