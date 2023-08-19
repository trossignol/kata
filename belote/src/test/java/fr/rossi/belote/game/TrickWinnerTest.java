package fr.rossi.belote.game;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.card.Figure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrickWinnerTest {

    private static void check(Card expectedWinner, TrickBuilder builder) {
        assertEquals(expectedWinner, builder.build().winner().card());

    }

    @Test
    void testTricksSameColorNonTrump() {
        check(new Card(Figure.AS, Color.PIQUE),
                new TrickBuilder(Color.COEUR)
                        .card(Figure.ROI, Color.PIQUE)
                        .card(Figure.AS, Color.PIQUE)
                        .card(Figure.NEUF, Color.PIQUE)
                        .card(Figure.VALET, Color.PIQUE));

    }

    @Test
    void testTricksSameColorTrump() {
        check(new Card(Figure.VALET, Color.PIQUE),
                new TrickBuilder(Color.PIQUE)
                        .card(Figure.ROI, Color.PIQUE)
                        .card(Figure.AS, Color.PIQUE)
                        .card(Figure.NEUF, Color.PIQUE)
                        .card(Figure.VALET, Color.PIQUE));

    }

    @Test
    void testTricksDifferentColorWithoutTrump() {
        check(new Card(Figure.ROI, Color.PIQUE),
                new TrickBuilder(Color.COEUR)
                        .card(Figure.ROI, Color.PIQUE)
                        .card(Figure.AS, Color.TREFLE)
                        .card(Figure.NEUF, Color.TREFLE)
                        .card(Figure.VALET, Color.PIQUE));

    }

    @Test
    void testTricksDifferentColorWithTrump() {
        check(new Card(Figure.NEUF, Color.COEUR),
                new TrickBuilder(Color.COEUR)
                        .card(Figure.ROI, Color.PIQUE)
                        .card(Figure.AS, Color.COEUR)
                        .card(Figure.NEUF, Color.COEUR)
                        .card(Figure.VALET, Color.PIQUE));

    }
}
