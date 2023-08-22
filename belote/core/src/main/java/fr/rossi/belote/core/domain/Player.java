package fr.rossi.belote.core.domain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.broadcast.EventConsumer;

import java.util.List;
import java.util.Optional;

public interface Player extends EventConsumer {

    String name();

    Team team();

    Player team(Team team);

    List<Card> hand();

    Player clearHand();

    Player addCards(List<Card> cards);

    Optional<Color> chooseTrump(Card card, boolean firstRun);

    Card play(Trick trick);
}
