package fr.rossi.belote.core.domain.event;

import fr.rossi.belote.core.domain.Team;

import java.util.Map;


public record GameEnd(Team winner, Status status, Map<Team, Integer> scores) implements Event {
    public enum Status {
        SIMPLE, TIMEOUT
    }
}
