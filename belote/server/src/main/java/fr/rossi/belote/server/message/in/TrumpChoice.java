package fr.rossi.belote.server.message.in;

import fr.rossi.belote.core.card.Color;
import lombok.Setter;

@Setter
public final class TrumpChoice extends InMessage {

    private Color color;

    public Color color() {
        return color;
    }
}
