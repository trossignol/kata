package fr.rossi.game2048;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class Grid {

    public static final Integer[] NEW_VALUES = { 2, 4 };

    @NonNull
    private final Integer[][] data;

    public Grid(int size) {
        this.data = new Integer[size][];
        range(0, size).forEach(row -> this.data[row] = new Integer[size]);
        this.setValue(2).setValue(2);
    }

    public List<Coord> getEmpty() {
        return range(0, this.data.length)
                .mapToObj(row -> range(0, this.data[row].length).mapToObj(col -> new Coord(row, col)))
                .flatMap(Function.identity())
                .filter(coord -> this.get(coord) == null)
                .toList();
    }

    Integer get(Coord coord) {
        return this.data[coord.row()][coord.col()];
    }

    Grid setValue() {
        return this.setValue(NEW_VALUES[new Random().nextInt(2)]);
    }

    Grid setValue(int value) {
        var empty = this.getEmpty();
        if (empty.isEmpty())
            return this;
        var coord = empty.get(new Random().nextInt(empty.size()));
        this.data[coord.row()][coord.col()] = Integer.valueOf(value);
        return this;
    }

    public int total() {
        return stream(this.data).flatMap(row -> stream(row))
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof Grid other
                ? Arrays.deepEquals(this.data, other.data)
                : false;
    }

    public String toString() {
        return stream(this.data)
                .map(row -> stream(row)
                        .map(value -> value == null ? " " : value.toString())
                        .collect(joining(" | ")))
                .collect(joining(System.lineSeparator()))
                + System.lineSeparator() + "-------------";
    }

    public static record Coord(int row, int col) {
    }
}
