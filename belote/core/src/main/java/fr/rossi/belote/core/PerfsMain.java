package fr.rossi.belote.core;

import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.game.Game;
import fr.rossi.belote.core.player.SimplePlayer;
import fr.rossi.belote.core.player.brain.BrainPlayer;
import fr.rossi.belote.core.player.brain.trumpchoice.TrumpChoice;
import fr.rossi.belote.core.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public class PerfsMain {

    public static void main(String[] args) {
        final AtomicInteger t1Winner = new AtomicInteger(0);
        var start = System.currentTimeMillis();

        var nb = 10_000;
        ThreadUtils.parallel(IntStream.range(0, nb).mapToObj(i -> (Runnable) () -> {
            if (run()) t1Winner.incrementAndGet();
        }).toList());

        var time = System.currentTimeMillis() - start;
        log.info("Team 1 won {}/{} ({} ms)", t1Winner, nb, ((double) time / nb));
        TrumpChoice.feedback();
    }

    private static boolean run() {
        var p1A = new BrainPlayer("1A");
        var p1B = new BrainPlayer("1B");
        var p2A = new SimplePlayer("2A");
        var p2B = new SimplePlayer("2B");

        var t1 = new Team(1, List.of(p1A, p1B));
        var t2 = new Team(2, List.of(p2A, p2B));
        var winner = new Game(List.of(t1, t2)).play();
        log.info("Winner={} !", winner);
        return winner.equals(t1);
    }
}
