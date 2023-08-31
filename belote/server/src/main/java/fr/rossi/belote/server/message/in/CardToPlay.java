package fr.rossi.belote.server.message.in;

import fr.rossi.belote.core.card.Card;
import lombok.Setter;

@Setter
public final class CardToPlay extends InMessage {

    private Card card;

    public Card card() {
        return this.card;
    }
}
