package fr.rossi.belote.core.domain.event;

import fr.rossi.belote.core.domain.CardsAndPlayers;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Team;

import java.util.Map;

public record TrickEnd(CardsAndPlayers cards, Player winner, Map<Team, Integer> runScores) implements Event {
}
