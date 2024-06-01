package fr.rossi.belote.core.player.brain.trumpchoice;

import fr.rossi.belote.core.card.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public record TrumpChoice(Color color, ChoiceCause cause, int handPoints) {

    private static final ConcurrentHashMap<ChoiceCause, Collection<Integer>> FEEDBACKS = new ConcurrentHashMap<>();

    public static void feedback() {
        FEEDBACKS.forEach((cause, feedbacks) -> log.info(" > {}: {} pts ({} round)",
                cause, (int) feedbacks.stream().mapToInt(s -> s).average().orElse(0) - (162 / 2), feedbacks.size()));
    }

    public void feedback(int points) {
        FEEDBACKS.computeIfAbsent(this.cause, c -> new ConcurrentLinkedQueue<>()).add(points);
    }
}
