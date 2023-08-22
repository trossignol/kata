package fr.rossi.belote.core.domain.event;

import fr.rossi.belote.core.domain.CardsAndPlayers;
import fr.rossi.belote.core.domain.Player;

public record TrickEnd(CardsAndPlayers cards, Player winner) implements Event {
}
