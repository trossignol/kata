package fr.rossi.belote.core.output;

import fr.rossi.belote.core.domain.broadcast.Broadcast;
import fr.rossi.belote.core.domain.event.*;
import fr.rossi.belote.core.utils.TextColor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

import static fr.rossi.belote.core.utils.TextColor.*;

@Slf4j
public class LogBroadcast extends Broadcast {

    private static void log(String title, TextColor color, String msg, Object... params) {
        log.info(color.to("[" + title + "] ") + msg, params);
    }

    private static void log(String title, TextColor color) {
        log(title, color, StringUtils.EMPTY);
    }

    private static <K, V> Map<K, V> sort(Map<K, V> map) {
        return new TreeMap<>(map);
    }

    @Override
    public void consume(Event event) {
        switch (event) {
            case GameStarted e -> log("Game started", GREEN, "players={}", e.players());
            case StartRound _ -> log("Start round", GREEN);
            case RoundStarted _ -> log("Round started", GREEN);
            case ChooseTrump e -> log("Choose trump", GREEN, "firstPlayer={}, card={}",
                    e.firstPlayer(), e.card());
            case TrumpChosen e -> log("{} choose {}", GREEN, "(card={})",
                    e.player(), e.chosenColor(), e.card());
            case TrickEnd e -> log("{} won trick", YELLOW, "{}",
                    e.winner(), e.cards());
            case RoundEnd e -> log("{} won round by {}", RED, "scores={} / table={} / total={}",
                    e.winner(), e.status(), sort(e.runScores()), sort(e.tableScores()), sort(e.scores()));
            case GameEnd e -> log("{} won game by {}", RED, "scores={}",
                    e.winner(), e.status(), sort(e.scores()));
        }
    }
}
