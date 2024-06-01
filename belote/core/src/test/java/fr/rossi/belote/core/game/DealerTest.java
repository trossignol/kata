package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.broadcast.Broadcast;
import fr.rossi.belote.core.exception.ActionTimeoutException;
import fr.rossi.belote.core.player.SimplePlayer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.rossi.belote.core.TestPlayers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DealerTest {

    @Test
    void testDeal() throws ActionTimeoutException {
        var players = PLAYERS.players();
        while (new Dealer(new Broadcast(), players, Card.getCards()).deal().isEmpty()) ;

        List<SimplePlayer> simplePlayers = List.of(P1, P2, P3, P4);
        simplePlayers.forEach(player -> assertEquals(8, player.hand().size()));
        assertEquals(32, simplePlayers.stream().map(SimplePlayer::hand).flatMap(List::stream).distinct().count());
    }
}
