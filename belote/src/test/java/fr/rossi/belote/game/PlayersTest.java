package fr.rossi.belote.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.rossi.belote.TestPlayers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayersTest {

    @Test
    void testConstructor() {
        assertEquals(List.of(P1, P2, P3, P4), new PlayersHandler(List.of(T1, T2)).players());
    }

    @Test
    void testNext1() {
        assertEquals(List.of(P2, P3, P4, P1), PLAYERS.next().players());
    }

    @Test
    void testNextP3() {
        assertEquals(List.of(P3, P4, P1, P2), PLAYERS.next(P3).players());
    }
}
