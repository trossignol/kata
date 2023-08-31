package fr.rossi.belote.core.domain.event;

import fr.rossi.belote.core.domain.Team;

import java.util.Map;


public final class RoundEnd implements Event {

    private final Team winner;
    private final Status status;
    private final Map<Team, Integer> tableScores;
    private final Map<Team, Integer> runScores;
    private Map<Team, Integer> scores;

    public RoundEnd(Team winner, Status status, Map<Team, Integer> tableScores, Map<Team, Integer> runScores) {
        super();
        this.winner = winner;
        this.status = status;
        this.tableScores = tableScores;
        this.runScores = runScores;
    }

    public Team winner() {
        return winner;
    }

    public Status status() {
        return status;
    }

    public Map<Team, Integer> tableScores() {
        return tableScores;
    }

    public Map<Team, Integer> runScores() {
        return runScores;
    }

    public Map<Team, Integer> scores() {
        return scores;
    }

    public RoundEnd scores(Map<Team, Integer> scores) {
        this.scores = scores;
        return this;
    }

    public enum Status {
        SIMPLE, IN, SHUTOUT
    }
}
