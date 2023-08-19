package fr.rossi.belote.game;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.card.Figure;
import fr.rossi.belote.domain.Player;

final class TrickBuilder {
    private final TrickImpl trick;

    public TrickBuilder(Color trump) {
        this.trick = new TrickImpl(trump);
    }
    
    public TrickBuilder card(Player player, Figure figure, Color color) {
        this.trick.addCard(player, new Card(figure, color));
        return this;
    }

    public TrickImpl build() {
        return this.trick;
    }
}