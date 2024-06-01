package fr.rossi.belote.core.domain.event;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Team;

public record TrumpChosen(Card card, Player player, Color chosenColor) implements Event {

    public boolean hasChoose(Team team) {
        return this.player.team().equals(team);
    }
}
