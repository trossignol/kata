package fr.rossi.belote;

import fr.rossi.belote.domain.Team;
import fr.rossi.belote.game.Game;
import fr.rossi.belote.player.SimplePlayer;
import fr.rossi.belote.player.manual.ManualPlayer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {
        var p1A = new ManualPlayer("1A");
        var p1B = new SimplePlayer("1B");
        var p2A = new SimplePlayer("2A");
        var p2B = new SimplePlayer("2B");

        var t1 = new Team(1, List.of(p1A, p1B));
        var t2 = new Team(2, List.of(p2A, p2B));

        var winner = new Game(List.of(t1, t2)).play();
        log.info("Winner={} !", winner);
    }
}
