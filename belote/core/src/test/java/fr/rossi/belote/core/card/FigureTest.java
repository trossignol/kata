package fr.rossi.belote.core.card;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FigureTest {

    private static void checkFiguresOrder(boolean trump, Figure... expected) {
        // Given
        final List<Figure> figures = List.of(Figure.values());
        final List<Figure> shuffled = new ArrayList<>(figures);
        Collections.shuffle(shuffled);

        // When
        shuffled.sort((f1, f2) -> f1.compareTo(f2, trump));

        // Then
        assertEquals(List.of(expected), shuffled);
    }

    @Test
    void testFiguresOrder() {
        checkFiguresOrder(false, Figure.values());
    }

    @Test
    void testFiguresOrderTrump() {
        checkFiguresOrder(true, Figure.VALET, Figure.NEUF, Figure.AS, Figure.DIX, Figure.ROI, Figure.DAME, Figure.HUIT,
                Figure.SEPT);
    }
}
