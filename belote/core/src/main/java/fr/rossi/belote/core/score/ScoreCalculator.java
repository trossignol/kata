package fr.rossi.belote.core.score;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.event.RoundEnd;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.rossi.belote.core.exception.TechnicalException.assertEquals;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
public record ScoreCalculator(Collection<Team> teams, Map<Team, Integer> inScores,
                              Team trumpTeam, Team lastWinner, Optional<Team> beloteTeam) {

    private static final int MAX_POINTS = Card.getCards().stream().mapToInt(card -> card.getPoints(Color.COEUR)).sum();
    private static final int LAST_10_POINTS = 10;
    private static final int BELOTE_POINTS = 20;
    private static final Map<RoundEnd.Status, Integer> SCORES_FOR_STATUS = Map.of(
            RoundEnd.Status.SHUTOUT, 252,
            RoundEnd.Status.IN, MAX_POINTS + LAST_10_POINTS);

    private static <T> void addScore(Map<T, Integer> scores, T key, int toAdd) {
        scores.compute(key, (t, s) -> Optional.ofNullable(s).orElse(0) + toAdd);
    }

    private static RoundEnd buildRoundEnd(Map<Team, Integer> scores, Optional<Team> beloteTeam,
                                          List<Team> losers, RoundEnd.Status status, Map<Team, Integer> tableScore) {
        var winner = scores.keySet().stream().filter(team -> !losers.contains(team)).findAny().orElseThrow();
        var runScores = scores.keySet().stream()
                .collect(toMap(identity(), team -> (losers.contains(team) ? 0 : SCORES_FOR_STATUS.get(status))
                        + beloteTeam.filter(team::equals).map(t -> BELOTE_POINTS).orElse(0)));
        return new RoundEnd(winner, status, tableScore, runScores);
    }

    public RoundEnd getScoresForPoints() {
        assertEquals("Error in sum of points", MAX_POINTS, inScores.values().stream().mapToInt(s -> s).sum());
        var scores = this.teams.stream().collect(Collectors.toMap(Function.identity(), team -> inScores.getOrDefault(team, 0)));

        // Last 10
        addScore(scores, lastWinner, LAST_10_POINTS);

        // Check shutout
        final List<Team> shutoutTeams = scores.entrySet().stream()
                .filter(e -> e.getValue() == 0).map(Map.Entry::getKey).toList();

        // Belote
        this.beloteTeam.ifPresent(team -> addScore(scores, team, BELOTE_POINTS));

        // Shutout
        if (!shutoutTeams.isEmpty()) {
            return buildRoundEnd(scores, this.beloteTeam, shutoutTeams, RoundEnd.Status.SHUTOUT, scores);
        }

        // Get winner
        var winner = scores.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey).orElseThrow();

        if (winner.equals(trumpTeam)) {
            return new RoundEnd(winner, RoundEnd.Status.SIMPLE, scores, scores);
        }

        return buildRoundEnd(scores, this.beloteTeam, List.of(this.trumpTeam), RoundEnd.Status.IN, scores);
    }
}
