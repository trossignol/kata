package fr.rossi.belote.core.score;

import fr.rossi.belote.core.domain.Team;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.rossi.belote.core.TestPlayers.T1;
import static fr.rossi.belote.core.TestPlayers.T2;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreCalculatorTest {

    private static void check(Team trumpTeam, Team lastWinnerTeam, Optional<Team> beloteTeam,
                              int t1InScore, int t2InScore, int t1OutScore, int t2OutScore) {
        assertEquals(Map.of(T1, t1OutScore, T2, t2OutScore),
                new ScoreCalculator(List.of(T1, T2),
                        Map.of(T1, t1InScore, T2, t2InScore),
                        trumpTeam, lastWinnerTeam, beloteTeam)
                        .getScoresForPoints().runScores());
    }

    private static void check(int t1InScore, int t2InScore, int t1OutScore, int t2OutScore) {
        check(T1, T1, Optional.empty(), t1InScore, t2InScore, t1OutScore, t2OutScore);
    }

    @Test
    void testScore() {
        check(100, 52, 110, 52);
    }

    @Test
    void testIn() {
        check(52, 100, 0, 162);
    }

    @Test
    void testSavedByLast10() {
        check(75, 77, 85, 77);
    }

    @Test
    void testShutout() {
        check(152, 0, 252, 0);
    }

    @Test
    void testShutoutWithMissingInMap() {
        assertEquals(Map.of(T1, 252, T2, 0),
                new ScoreCalculator(List.of(T1, T2), Map.of(T1, 152), T1, T1, Optional.empty())
                        .getScoresForPoints().runScores());
    }

    @Test
    void testBelote() {
        check(T1, T1, Optional.of(T2), 100, 52, 110, 72);
    }

    @Test
    void testSavedByBelote() {
        check(T1, T2, Optional.of(T1), 72, 80, 92, 90);
    }

    @Test
    void testInWithBelote() {
        check(T1, T2, Optional.of(T1), 50, 102, 20, 162);
    }

    @Test
    void testShutoutWithBelote() {
        check(T1, T1, Optional.of(T2), 152, 0, 252, 20);
    }
}
