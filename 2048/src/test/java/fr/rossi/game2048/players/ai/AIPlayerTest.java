package fr.rossi.game2048.players.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.rossi.game2048.Grid;

public class AIPlayerTest {

    @Test
    void simulate_values() {
        // given
        final Integer[][] data = { { 1, null }, { null, 1 } };

        // when
        var grids = DirectionNode.simulateValues(new Grid(data));

        // then
        assertEquals(4, grids.size());
        var expectedGrids = List.of(
                new Integer[][] { { 1, 2 }, { null, 1 } },
                new Integer[][] { { 1, 4 }, { null, 1 } },
                new Integer[][] { { 1, null }, { 2, 1 } },
                new Integer[][] { { 1, null }, { 4, 1 } })
                .stream().map(Grid::new).toList();
        assertTrue(grids.containsAll(expectedGrids));
    }
}
