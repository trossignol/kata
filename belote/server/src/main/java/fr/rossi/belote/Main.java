package fr.rossi.belote;

import fr.rossi.belote.server.GameService;
import fr.rossi.belote.server.message.StartGame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        var winner = new GameService().startGame("", new StartGame());
        log.info("Winner={} !", winner);
    }
}
