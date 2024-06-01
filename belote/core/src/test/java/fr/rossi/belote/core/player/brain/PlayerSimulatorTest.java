package fr.rossi.belote.core.player.brain;

import fr.rossi.belote.core.TestPlayers;
import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;
import fr.rossi.belote.core.domain.CardAndPlayer;
import fr.rossi.belote.core.domain.CardsAndPlayers;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerSimulatorTest {

    private static final List<Card> MY_HAND = List.of(new Card(Figure.ROI, Color.COEUR),
            new Card(Figure.DAME, Color.COEUR),
            new Card(Figure.AS, Color.PIQUE),
            new Card(Figure.DAME, Color.PIQUE));
    private static final Card TRUMP_CARD = new Card(Figure.NEUF, Color.PIQUE);
    private static final PlayerSimulator PS = new PlayerSimulator(TestPlayers.P2, TestPlayers.P1, MY_HAND,
            new TrumpChosen(TRUMP_CARD, TestPlayers.P3, TRUMP_CARD.color()));


    private static void checkRemoved(PlayerSimulator ps, CardsAndPlayers trick, List<Card> cards, Color... fullColor) {
        checkRemoved(ps, List.of(trick), cards, fullColor);
    }

    private static void checkRemoved(PlayerSimulator ps, List<CardsAndPlayers> tricks, List<Card> cards, Color... fullColor) {
        assertEquals(MY_HAND.size() - tricks.size(), ps.nbCardsInHand());
        var missing = new HashSet<>(cards);
        missing.addAll(MY_HAND);
        tricks.stream().flatMap(List::stream).map(CardAndPlayer::card).forEach(missing::add);
        missing.add(TRUMP_CARD);
        Arrays.stream(fullColor)
                .forEach(color -> Arrays.stream(Figure.values())
                        .forEach(figure -> missing.add(new Card(figure, color))));
        assertEquals(Card.nbCards() - missing.size(), ps.getPotentialHand().size(),
                () -> String.format("Wrong size for hand=%s", ps));
        var found = missing.stream().filter(ps.getPotentialHand()::contains).toList();
        assertTrue(found.isEmpty(),
                () -> String.format("Should not contains: %s (hand=%s)", found, ps));
    }

    @Test
    void init() {
        assertEquals(Card.nbCards() - MY_HAND.size() - 1, PS.getPotentialHand().size());
    }

    @Test
    void initTrumpGetter() {
        var ps = new PlayerSimulator(TestPlayers.P2, TestPlayers.P1, MY_HAND,
                new TrumpChosen(TRUMP_CARD, TestPlayers.P2, TRUMP_CARD.color()));
        assertEquals(Card.nbCards() - MY_HAND.size(), ps.getPotentialHand().size());
    }

    @Test
    void testTrick1() {
        var trick = new CardsAndPlayers()
                .add(new Card(Figure.AS, Color.PIQUE), TestPlayers.P1)
                .add(new Card(Figure.ROI, Color.PIQUE), TestPlayers.P2);
        var ps = new PlayerSimulator(PS, trick);
        // AS-PIQUE already known (because in "MY HAND") but ROI-PIQUE should be removed
        assertEquals(PS.getPotentialHand().size() - 1, ps.getPotentialHand().size());
        checkRemoved(ps, trick, emptyList());
    }

    @Test
    void testTrickNoMorePique() {
        var trick = new CardsAndPlayers()
                .add(new Card(Figure.AS, Color.PIQUE), TestPlayers.P1)
                .add(new Card(Figure.DAME, Color.TREFLE), TestPlayers.P2);
        var ps = new PlayerSimulator(PS, trick);
        // All PIQUE should be removed
        checkRemoved(ps, trick, emptyList(), Color.PIQUE);
    }

    @Test
    void testTrickNoMoreTrick() {
        var trick = new CardsAndPlayers()
                .add(new Card(Figure.DAME, Color.COEUR), TestPlayers.P1)
                .add(new Card(Figure.HUIT, Color.CARREAU), TestPlayers.P2);
        var ps = new PlayerSimulator(PS, trick);
        // All COEUR and PIQUE (trump) should be removed
        checkRemoved(ps, trick, emptyList(), Color.COEUR, Color.PIQUE);
    }

    @Test
    void testTrickNoMoreUpperTrump() {
        var trick = new CardsAndPlayers()
                .add(new Card(Figure.DAME, Color.TREFLE), TestPlayers.P4)
                .add(new Card(Figure.AS, Color.PIQUE), TestPlayers.P1)
                .add(new Card(Figure.ROI, Color.PIQUE), TestPlayers.P2);
        var ps = new PlayerSimulator(PS, trick);
        // All TREFLE and PIQUE (trump) upper than AS should be removed
        checkRemoved(ps, trick, List.of(
                new Card(Figure.VALET, Color.PIQUE),
                new Card(Figure.NEUF, Color.PIQUE)
        ), Color.TREFLE);
    }

    @Test
    void testTrickWithTrump() {
        var trick = new CardsAndPlayers()
                .add(new Card(Figure.DAME, Color.TREFLE), TestPlayers.P4)
                .add(new Card(Figure.DAME, Color.PIQUE), TestPlayers.P1)
                .add(new Card(Figure.DIX, Color.PIQUE), TestPlayers.P2);
        var ps = new PlayerSimulator(PS, trick);
        // All TREFLE only
        checkRemoved(ps, trick, emptyList(), Color.TREFLE);
    }

    @Test
    void testTrickOpponentLeader() {
        var trick = new CardsAndPlayers()
                .add(new Card(Figure.SEPT, Color.COEUR), TestPlayers.P4)
                .add(new Card(Figure.ROI, Color.COEUR), TestPlayers.P1)
                .add(new Card(Figure.ROI, Color.PIQUE), TestPlayers.P2);
        var ps = new PlayerSimulator(PS, trick);
        // All TREFLE only
        checkRemoved(ps, trick, emptyList(), Color.COEUR);
    }

    @Test
    void test2Tricks() {
        var trick1 = new CardsAndPlayers()
                .add(new Card(Figure.SEPT, Color.COEUR), TestPlayers.P4)
                .add(new Card(Figure.ROI, Color.COEUR), TestPlayers.P1)
                .add(new Card(Figure.ROI, Color.PIQUE), TestPlayers.P2)
                .add(new Card(Figure.DIX, Color.COEUR), TestPlayers.P3);
        var ps1 = new PlayerSimulator(PS, trick1);
        // All TREFLE only
        checkRemoved(ps1, trick1, emptyList(), Color.COEUR);

        var trick2 = new CardsAndPlayers()
                .add(new Card(Figure.AS, Color.TREFLE), TestPlayers.P3)
                .add(new Card(Figure.DAME, Color.PIQUE), TestPlayers.P4)
                .add(new Card(Figure.SEPT, Color.TREFLE), TestPlayers.P1)
                .add(new Card(Figure.DAME, Color.TREFLE), TestPlayers.P2);
        var ps2 = new PlayerSimulator(ps1, trick2);
        // All TREFLE only
        assertTrue(ps2.getPotentialHand().size() <= ps1.getPotentialHand().size());
        checkRemoved(ps2, List.of(trick1, trick2), emptyList(), Color.COEUR);
    }
}
