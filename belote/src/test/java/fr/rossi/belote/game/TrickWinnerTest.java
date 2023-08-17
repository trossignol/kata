package fr.rossi.belote.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.card.Figure;

public class TrickWinnerTest {

    @Test
    public void testTricksSameColorNonTrump() {
        assertEquals(new Card(Figure.AS, Color.PIQUE),
                new TrickBuilder(Color.COEUR)
                        .card(Figure.ROI, Color.PIQUE)
                        .card(Figure.AS, Color.PIQUE)
                        .card(Figure.NEUF, Color.PIQUE)
                        .card(Figure.VALET, Color.PIQUE)
                        .build().getWinner());

    }

    @Test
    public void testTricksSameColorTrump() {
        assertEquals(new Card(Figure.VALET, Color.PIQUE),
                new TrickBuilder(Color.PIQUE)
                        .card(Figure.ROI, Color.PIQUE)
                        .card(Figure.AS, Color.PIQUE)
                        .card(Figure.NEUF, Color.PIQUE)
                        .card(Figure.VALET, Color.PIQUE)
                        .build().getWinner());

    }

    @Test
    public void testTricksDifferentColorWithoutTrump() {
        assertEquals(new Card(Figure.ROI, Color.PIQUE),
                new TrickBuilder(Color.COEUR)
                        .card(Figure.ROI, Color.PIQUE)
                        .card(Figure.AS, Color.TREFLE)
                        .card(Figure.NEUF, Color.TREFLE)
                        .card(Figure.VALET, Color.PIQUE)
                        .build().getWinner());

    }

    @Test
    public void testTricksDifferentColorWithTrump() {
        assertEquals(new Card(Figure.NEUF, Color.COEUR),
                new TrickBuilder(Color.COEUR)
                        .card(Figure.ROI, Color.PIQUE)
                        .card(Figure.AS, Color.COEUR)
                        .card(Figure.NEUF, Color.COEUR)
                        .card(Figure.VALET, Color.PIQUE)
                        .build().getWinner());

    }
}
