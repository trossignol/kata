package fr.rossi.belote.score;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.domain.Team;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;

import static fr.rossi.belote.exception.TechnicalException.assertEquals;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Log
public record ScoreCalculator(Map<Team, Integer> inScores,
                              Team trumpTeam, Team lastWinner, Optional<Team> beloteTeam) {

    private static final int MAX_POINTS = Card.getCards().stream().mapToInt(card -> card.getPoints(Color.COEUR)).sum();
    private static final int LAST_10_POINTS = 10;
    private static final int BELOTE_POINTS = 20;
    private static final int SHUTOUT_POINTS = 252;

    private static <T> void addScore(Map<T, Integer> scores, T key, int toAdd) {
        scores.compute(key, (t, s) -> Optional.ofNullable(s).orElseThrow() + toAdd);
    }

    public Map<Team, Integer> getScoresForPoints() {
        assertEquals("Error in sum of points", MAX_POINTS, inScores.values().stream().mapToInt(s -> s).sum());
        var scores = new HashMap<>(inScores);

        // Shutout
        final List<Team> shutoutTeams = scores.entrySet().stream()
                .filter(e -> e.getValue() == 0).map(Map.Entry::getKey).toList();
        if (!shutoutTeams.isEmpty()) {
            log.info(shutoutTeams + " is shutout!");
            return buildScoresMap(team -> shutoutTeams.contains(team) ? 0 : SHUTOUT_POINTS);
        }

        // Last 10
        addScore(scores, lastWinner, LAST_10_POINTS);
        this.beloteTeam.ifPresent(team -> addScore(scores, team, BELOTE_POINTS));

        // Get winner
        var winner = scores.entrySet().stream()
                .max((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()))
                .map(Map.Entry::getKey).orElseThrow();

        if (winner.equals(trumpTeam)) {
            return scores;
        }

        // In
        log.info(trumpTeam + " is in! (" + scores.get(trumpTeam) + ")");
        return buildScoresMap(team -> team.equals(trumpTeam) ? 0 : MAX_POINTS + LAST_10_POINTS);
    }

    private Map<Team, Integer> buildScoresMap(ToIntFunction<Team> getPoints) {
        return this.inScores.keySet().stream()
                .collect(toMap(identity(), team -> getPoints.applyAsInt(team)
                        + this.beloteTeam.filter(team::equals).map(t -> BELOTE_POINTS).orElse(0)));
    }
}
