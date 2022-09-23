package fr.rossi.game2048;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Move {

    private final Direction direction;
    private final Grid grid;

    public Optional<Grid> apply() {
        var initData = this.grid.getData();
        var rows = matrixToRows(direction, initData)
                .stream()
                .map(Move::compress)
                .toList();

        var newData = rowsToMatrix(direction, rows);
        return Arrays.deepEquals(initData, newData)
                ? Optional.empty()
                : Optional.of(new Grid(newData));
    }

    static Integer[] compress(Integer... data) {
        var values = stream(data).filter(Objects::nonNull).toList();

        var output = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if (i == values.size() - 1) {
                output.add(values.get(i));
                break;
            }
            if (values.get(i).equals(values.get(i + 1))) {
                output.add(values.get(i).intValue() * 2);
                i++;
            } else {
                output.add(values.get(i));
            }
        }
        return range(0, data.length)
                .mapToObj(i -> i < output.size() ? output.get(i) : null)
                .toArray(Integer[]::new);
    }

    static List<Integer[]> matrixToRows(Direction direction, Integer[][] data) {
        return switch (direction) {
            case UP, DOWN -> range(0, data.length)
                    .mapToObj(col -> {
                        var array = range(0, data.length)
                                .mapToObj(row -> data[row][col])
                                .toArray(Integer[]::new);
                        if (direction == Direction.DOWN)
                            ArrayUtils.reverse(array);
                        return array;
                    }).toList();

            case LEFT, RIGHT -> range(0, data.length)
                    .mapToObj(row -> {
                        var array = range(0, data.length)
                                .mapToObj(col -> data[row][col])
                                .toArray(Integer[]::new);
                        if (direction == Direction.RIGHT)
                            ArrayUtils.reverse(array);
                        return array;
                    }).toList();
        };
    }

    static Integer[][] rowsToMatrix(Direction direction, List<Integer[]> rows) {
        return switch (direction) {
            case UP, DOWN:
                var data = range(0, rows.size())
                        .mapToObj(col -> {
                            final Integer[] array = rows.stream().map(row -> row[col]).toArray(Integer[]::new);
                            return array;
                        })
                        .toArray(Integer[][]::new);
                if (direction == Direction.DOWN)
                    ArrayUtils.reverse(data);
                yield data;
            case LEFT, RIGHT:
                yield rows.stream()
                        .map(row -> {
                            if (direction == Direction.RIGHT)
                                ArrayUtils.reverse(row);
                            return row;
                        })
                        .toArray(Integer[][]::new);
        };
    }

    static boolean isOver(Grid grid) {
        return grid.getEmpty().isEmpty()
                && Stream.of(Direction.UP, Direction.LEFT)
                        .map(direction -> new Move(direction, grid))
                        .map(Move::apply)
                        .allMatch(Optional::isEmpty);
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
