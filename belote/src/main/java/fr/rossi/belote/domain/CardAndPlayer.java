package fr.rossi.belote.domain;

import fr.rossi.belote.card.Card;

public record CardAndPlayer(Card card, Player player) {

    @Override
    public String toString() {
        return String.format("%s: %s", this.player, this.card);
    }
}
