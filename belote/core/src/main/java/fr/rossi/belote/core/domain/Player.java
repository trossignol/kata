package fr.rossi.belote.core.domain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.broadcast.EventConsumer;
import fr.rossi.belote.core.exception.ActionTimeoutException;

import java.util.List;
import java.util.Optional;

public interface Player extends EventConsumer {

    String name();

    Player gameId(String gameId);

    Team team();

    default Player partner() {
        return this.team().players().stream().filter(p -> !this.equals(p)).findAny().orElseThrow();
    }

    Player team(Team team);

    Player clearHand();

    Player addCards(List<Card> cards);

    Optional<Color> chooseTrump(Card card, boolean firstRun) throws ActionTimeoutException;

    Card play(Trick trick) throws ActionTimeoutException;
}
