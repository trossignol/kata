package fr.rossi.belote.core.domain.event;

import fr.rossi.belote.core.domain.Player;

import java.util.List;

public record GameStarted(List<Player> players) implements Event {
}
