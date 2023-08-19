package fr.rossi.belote.player;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.card.Figure;
import fr.rossi.belote.exception.TechnicalException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SimplePlayerTest {

    private static List<Card> cards(int nb) {
        return IntStream.range(0, nb).mapToObj(i -> new Card(Figure.ROI, Color.CARREAU)).toList();
    }

    @Test
    void testTooManyCards() {
        var player = new SimplePlayer("1");
        player.addCards(cards(7));

        var newCards = cards(2);
        assertThrows(TechnicalException.class, () -> player.addCards(newCards));
    }
}
