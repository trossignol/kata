package fr.rossi.belote.domain.event;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.domain.Player;

public record ChooseTrump(Card card, Player firstPlayer) implements Event {
}