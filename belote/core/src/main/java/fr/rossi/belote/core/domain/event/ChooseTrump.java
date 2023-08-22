package fr.rossi.belote.core.domain.event;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.Player;

public record ChooseTrump(Card card, Player firstPlayer) implements Event {
}