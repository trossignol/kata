package fr.rossi.belote.core.game;

import org.junit.jupiter.api.Test;

import static fr.rossi.belote.core.TestPlayers.TEAMS;

class GameTest {

    @Test
    void testPlay() {

        new Game(TEAMS).play();
    }
}
