package fr.rossi.belote.core.player.brain.trumpchoice;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public enum ChoiceCause {
    TRUMP_4(0.9, 4, 0, 0),
    TRUMP_34PTS(0.8, 2, 34, 0),
    TRUMP_3_AND_1_AS(0.7, 3, 25, 1),
    TRUMP_21PTS_AND_2_AS(0.6, 2, 21, 2);

    private static final Comparator<ChoiceCause> SORTER = Comparator.comparingDouble(c -> c.confidence);
    private static final List<ChoiceCause> SORTED = Arrays.stream(values()).sorted(SORTER.reversed()).toList();
    private final double confidence;
    private final Predicate<TrumpChoiceParams> predicate;

    ChoiceCause(double confidence, Predicate<TrumpChoiceParams> predicate) {
        this.confidence = confidence;
        this.predicate = predicate;
    }

    ChoiceCause(double confidence, int minTrumpCount, int minTrumpPoints, int minOtherAsCount) {
        this(confidence, params -> params.getTrumpCards().size() >= minTrumpCount
                && params.getTrumpPoints() >= minTrumpPoints && params.getOtherAsCount() >= minOtherAsCount);
    }

    static Optional<ChoiceCause> get(TrumpChoiceParams params, double minConfidence) {
        return SORTED.stream()
                .filter(c -> c.confidence >= minConfidence)
                .filter(c -> c.test(params))
                .findFirst();
    }

    private boolean test(TrumpChoiceParams params) {
        return this.predicate.test(params);
    }
}