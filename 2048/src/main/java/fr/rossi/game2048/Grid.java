package fr.rossi.game2048;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Grid {
    private final Integer[][] data;

    public Grid(int size) {
        this.data = new Integer[size][];
        range(0, size).forEach(row -> this.data[row] = new Integer[size]);
        this.setValue(2).setValue(2);
    }

    public String toString() {
        return stream(this.data)
                .map(row -> stream(row)
                        .map(value -> value == null ? " " : value.toString())
                        .collect(joining(" | ")))
                .collect(joining(System.lineSeparator()));
    }

    List<Coord> getEmpty() {
        return range(0, this.data.length)
                .mapToObj(row -> range(0, this.data[row].length).mapToObj(col -> new Coord(row, col)))
                .flatMap(Function.identity())
                .filter(coord -> this.get(coord) == null)
                .collect(toList());
    }

    Integer get(Coord coord) {
        return this.data[coord.row()][coord.col()];
    }

    Grid setValue() {
        return this.setValue((new Random().nextInt(2) + 1) * 2);
    }

    Grid setValue(int value) {
        final List<Coord> empty = this.getEmpty();
        final Coord coord = empty.get(new Random().nextInt(empty.size()));
        this.data[coord.row()][coord.col()] = Integer.valueOf(value);
        return this;
    }

    public static record Coord(int row, int col) {
    }
}
