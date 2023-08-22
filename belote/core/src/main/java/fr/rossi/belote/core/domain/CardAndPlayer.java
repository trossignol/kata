package fr.rossi.belote.core.domain;

import fr.rossi.belote.core.card.Card;

public record CardAndPlayer(Card card, Player player) {

    @Override
    public String toString() {
        return String.format("%s: %s", this.player, this.card);
    }
}
