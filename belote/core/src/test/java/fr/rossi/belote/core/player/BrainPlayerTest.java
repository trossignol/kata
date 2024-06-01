package fr.rossi.belote.core.player;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;
import fr.rossi.belote.core.player.brain.BrainPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrainPlayerTest {

    @Test
    void testChooseTrumpYes() {
        var player = new BrainPlayer("1");
        player.addCards(List.of(
                new Card(Figure.VALET, Color.COEUR),
                new Card(Figure.AS, Color.COEUR),
                new Card(Figure.DAME, Color.COEUR),
                new Card(Figure.VALET, Color.PIQUE),
                new Card(Figure.AS, Color.CARREAU)));

        var oColor = player.chooseTrump(new Card(Figure.ROI, Color.COEUR), true);
        assertTrue(oColor.isPresent());
        assertEquals(Color.COEUR, oColor.get());
    }
}
