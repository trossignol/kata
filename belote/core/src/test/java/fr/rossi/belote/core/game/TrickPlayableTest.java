package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static fr.rossi.belote.core.TestPlayers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TrickPlayableTest {

    private static final List<Card> HAND = List.of(new Card(Figure.DAME, Color.PIQUE),
            new Card(Figure.AS, Color.CARREAU), new Card(Figure.DAME, Color.CARREAU),
            new Card(Figure.VALET, Color.COEUR), new Card(Figure.HUIT, Color.COEUR));

    static {
        P3.clearHand().addCards(HAND);
    }

    private static Collection<Card> play(Color trump, Figure f1, Color c1, Figure f2, Color c2) {
        return new TrickBuilder(trump).card(P1, f1, c1).card(P2, f2, c2).build().playableCards(P3, P3.hand());
    }

    @Test
    void testFirstPlayer() {
        assertEquals(HAND,
                new TrickBuilder(Color.COEUR)
                        .build()
                        .playableCards(P3, P3.hand()));
    }

    @Test
    void testPlayerHasNotColorButHasTrumpAndPartnerLeader() {
        assertEquals(HAND, play(Color.COEUR,
                Figure.AS, Color.TREFLE,
                Figure.ROI, Color.TREFLE));
    }

    @Test
    void testSimpleCut() {
        assertEquals(List.of(new Card(Figure.VALET, Color.COEUR), new Card(Figure.HUIT, Color.COEUR)),
                play(Color.COEUR,
                        Figure.ROI, Color.TREFLE,
                        Figure.AS, Color.TREFLE));
    }

    @Test
    void testCutUpper() {
        assertEquals(List.of(new Card(Figure.VALET, Color.COEUR)),
                play(Color.COEUR,
                        Figure.AS, Color.TREFLE,
                        Figure.ROI, Color.COEUR));
    }

    @Test
    void testTrumpGoLower() {
        assertEquals(List.of(new Card(Figure.AS, Color.CARREAU)),
                play(Color.CARREAU,
                        Figure.HUIT, Color.CARREAU,
                        Figure.ROI, Color.CARREAU));
    }

    @Test
    void testCutLower() {
        assertEquals(List.of(new Card(Figure.DAME, Color.PIQUE)),
                play(Color.PIQUE,
                        Figure.AS, Color.TREFLE,
                        Figure.ROI, Color.PIQUE));
    }

    @Test
    void testColorEvenIfCut() {
        assertEquals(List.of(new Card(Figure.DAME, Color.PIQUE)),
                play(Color.CARREAU,
                        Figure.AS, Color.PIQUE,
                        Figure.ROI, Color.CARREAU));
    }

    @Test
    void testTrumpWantedButHasNotTrump() {
        assertEquals(HAND,
                play(Color.TREFLE,
                        Figure.HUIT, Color.TREFLE,
                        Figure.ROI, Color.TREFLE));
    }
}
