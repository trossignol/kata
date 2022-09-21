package fr.rossi.game2048;

import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Move {

    private final Direction direction;
    private final Grid grid;

    public Grid apply() {
        var rows = matrixToRows(direction, this.grid.getData())
                .stream()
                .map(Move::compress)
                .toList();
        return new Grid(rowsToMatrix(direction, rows));
    }

    static Integer[] compress(Integer... data) {
        var values = Arrays.stream(data).filter(Objects::nonNull).toList();

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
        System.out.println(ArrayUtils.toString(data));
        System.out.println(output);
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

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
