package fr.rossi.belote.game;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.domain.Player;
import fr.rossi.belote.domain.broadcast.Broadcast;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.rossi.belote.TestPlayers.PLAYERS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DealerTest {

    @Test
    void testDeal() {
        final List<Player> players = PLAYERS.players();
        while (new Dealer(new Broadcast(), players, Card.getCards()).deal().isEmpty()) ;

        players.forEach(player -> assertEquals(8, player.hand().size()));
        assertEquals(32, players.stream().map(Player::hand).flatMap(List::stream).distinct().count());
    }
}
