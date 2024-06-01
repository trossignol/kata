package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;
import fr.rossi.belote.core.domain.Player;

final class TrickBuilder {
    private TrickImpl trick;

    public TrickBuilder(Color trump) {
        this.trick = new TrickImpl(trump);
    }

    public TrickBuilder card(Player player, Figure figure, Color color) {
        this.trick = this.trick.addCard(player, new Card(figure, color));
        return this;
    }

    public TrickImpl build() {
        return this.trick;
    }
}