package fr.rossi.game2048;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.rossi.game2048.Move.Direction;

public class MoveTest {

    @Test
    void complete_apply() {
        // given
        final Integer[][] data = { { null, 2, 2, 2 }, { 2, null, null, 4 }, { null, null, 2, 2 },
                { null, null, null, 2 } };
        var grid = new Grid(data);
        var move = new Move(Direction.UP, grid);

        // when
        var newGrid = move.apply();
        System.out.println(grid);
        System.out.println("-------------------------");
        System.out.println(newGrid);

        // then
        final Integer[][] expected = { { 2, 2, 4, 2 }, { null, null, null, 4 }, { null, null, null, 4 },
                { null, null, null, null } };
        assertArrayEquals(expected, newGrid.getData());
    }

    @Test
    void simple_compress() {
        assertArrayEquals(Move.compress(1, 2), new Integer[] { 1, 2 });
        assertArrayEquals(Move.compress(2, 2), new Integer[] { 4, null });
        assertArrayEquals(Move.compress(2, 4, 2), new Integer[] { 2, 4, 2 });
        assertArrayEquals(Move.compress(2, null, null, 2), new Integer[] { 4, null, null, null });
        assertArrayEquals(Move.compress(2, null, null, 1), new Integer[] { 2, 1, null, null });
    }

    @Test
    void matrix_vs_rows_UP() {
        check(Direction.UP, List.of(new Integer[] { 1, 3 }, new Integer[] { 2, 4 }));
    }

    @Test
    void matrix_vs_rows_DOWN() {
        check(Direction.DOWN, List.of(new Integer[] { 3, 1 }, new Integer[] { 4, 2 }));
    }

    @Test
    void matrix_vs_rows_LEFT() {
        check(Direction.LEFT, List.of(new Integer[] { 1, 2 }, new Integer[] { 3, 4 }));
    }

    @Test
    void matrix_vs_rows_RIGHT() {
        check(Direction.RIGHT, List.of(new Integer[] { 2, 1 }, new Integer[] { 4, 3 }));
    }

    private static void check(Direction direction, List<Integer[]> expected) {
        // given
        final Integer[][] data = { { 1, 2 }, { 3, 4 } };

        // when
        var rows = Move.matrixToRows(direction, data);

        // then
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), rows.get(i));
        }

        // when
        var outputData = Move.rowsToMatrix(direction, rows);

        // then
        assertArrayEquals(data, outputData);
    }
}
