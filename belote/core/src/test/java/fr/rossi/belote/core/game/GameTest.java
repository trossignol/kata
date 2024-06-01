package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.exception.ActionTimeoutException;
import fr.rossi.belote.core.player.SimplePlayer;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static fr.rossi.belote.core.TestPlayers.T2;
import static fr.rossi.belote.core.TestPlayers.TEAMS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {

    @Test
    void testPlay() {
        for (int i = 0; i < 100; i++)
            new Game(TEAMS).play();
    }

    @Test
    void testTimeout() {
        var player1 = new SimplePlayer("player1") {
            protected Card cardToPlay(Trick trick, Collection<Card> playableCards) throws ActionTimeoutException {
                throw new ActionTimeoutException("play", this.name());
            }
        };
        var game = new Game(List.of(new Team(1, List.of(player1, new SimplePlayer("player3"))), T2));
        assertEquals(T2, game.play());
    }
}
