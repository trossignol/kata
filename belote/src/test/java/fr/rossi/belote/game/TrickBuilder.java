package fr.rossi.belote.game;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.card.Figure;

public final class TrickBuilder {
    private final Trick trick;

    public TrickBuilder(Color trump) {
        this.trick = new Trick(new Round(trump));
    }

    public TrickBuilder card(Figure figure, Color color) {
        this.trick.addCard(new Card(figure, color));
        return this;
    }

    public Trick build() {
        return this.trick;
    }
}