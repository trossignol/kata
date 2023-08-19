package fr.rossi.belote.game;

import org.junit.jupiter.api.Test;

import static fr.rossi.belote.TestPlayers.TEAMS;

class GameTest {

    @Test
    void testPlay() {

        new Game(TEAMS).play();
    }
}
