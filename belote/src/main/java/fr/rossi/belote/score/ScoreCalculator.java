package fr.rossi.belote.score;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.domain.Team;
import fr.rossi.belote.domain.event.RoundEnd;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static fr.rossi.belote.exception.TechnicalException.assertEquals;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
public record ScoreCalculator(Map<Team, Integer> inScores,
                              Team trumpTeam, Team lastWinner, Optional<Team> beloteTeam) {

    private static final int MAX_POINTS = Card.getCards().stream().mapToInt(card -> card.getPoints(Color.COEUR)).sum();
    private static final int LAST_10_POINTS = 10;
    private static final int BELOTE_POINTS = 20;
    private static final Map<RoundEnd.Status, Integer> SCORES_FOR_STATUS = Map.of(
            RoundEnd.Status.SHUTOUT, 252,
            RoundEnd.Status.IN, MAX_POINTS + LAST_10_POINTS);

    private static <T> void addScore(Map<T, Integer> scores, T key, int toAdd) {
        scores.compute(key, (t, s) -> Optional.ofNullable(s).orElseThrow() + toAdd);
    }

    public RoundEnd getScoresForPoints() {
        assertEquals("Error in sum of points", MAX_POINTS, inScores.values().stream().mapToInt(s -> s).sum());
        var scores = new HashMap<>(inScores);

        // Shutout
        final List<Team> shutoutTeams = scores.entrySet().stream()
                .filter(e -> e.getValue() == 0).map(Map.Entry::getKey).toList();
        if (!shutoutTeams.isEmpty()) {
            return buildRoundEnd(shutoutTeams, RoundEnd.Status.SHUTOUT);
        }

        // Last 10
        addScore(scores, lastWinner, LAST_10_POINTS);
        this.beloteTeam.ifPresent(team -> addScore(scores, team, BELOTE_POINTS));

        // Get winner

        var winner = scores.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey).orElseThrow();

        if (winner.equals(trumpTeam)) {
            return new RoundEnd(winner, RoundEnd.Status.SIMPLE, scores);
        }

        return buildRoundEnd(List.of(this.trumpTeam), RoundEnd.Status.IN);
    }

    private RoundEnd buildRoundEnd(List<Team> losers, RoundEnd.Status status) {
        var winner = this.inScores.keySet().stream().filter(team -> !losers.contains(team)).findAny().orElseThrow();
        var scores = this.inScores.keySet().stream()
                .collect(toMap(identity(), team -> (losers.contains(team) ? 0 : SCORES_FOR_STATUS.get(status))
                        + this.beloteTeam.filter(team::equals).map(t -> BELOTE_POINTS).orElse(0)));
        return new RoundEnd(winner, status, scores);
    }
}
