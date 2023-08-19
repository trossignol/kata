package fr.rossi.belote.domain.event;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.domain.Player;

public record TrumpChosen(Card card, Player player, Color chosenColor) implements Event {
}
