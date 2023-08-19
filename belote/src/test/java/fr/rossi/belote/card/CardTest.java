package fr.rossi.belote.card;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

    @Test
    void testCards() {
        final List<Card> cards = Card.getCards();
        assertEquals(32, cards.size());
        assertEquals(32, new HashSet<>(cards).size());

        for (Color color : Color.values()) {
            assertEquals(8, cards.stream().filter(card -> card.color() == color).count());
        }

        for (Figure figure : Figure.values()) {
            assertEquals(4, cards.stream().filter(card -> card.figure() == figure).count());
        }
    }
}
