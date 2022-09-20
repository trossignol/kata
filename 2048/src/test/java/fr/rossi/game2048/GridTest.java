package fr.rossi.game2048;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class GridTest {

    @Test
    void checkAllEmpty() {
        // given
        final int size = 4;

        // when
        final Grid grid = new Grid(size);
        // then
        assertEquals(size * size - 2, grid.getEmpty().size());

        // when
        grid.setValue();
        // then
        assertEquals(size * size - 3, grid.getEmpty().size());

        // when
        grid.setValue();
        // then
        assertEquals(size * size - 4, grid.getEmpty().size());
    }
}
