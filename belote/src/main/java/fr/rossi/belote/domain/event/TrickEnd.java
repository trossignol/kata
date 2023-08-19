package fr.rossi.belote.domain.event;

import fr.rossi.belote.domain.CardsAndPlayers;
import fr.rossi.belote.domain.Player;

public record TrickEnd(CardsAndPlayers cards, Player winner) implements Event {
}
