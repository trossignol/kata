package fr.rossi.belote.domain;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.domain.broadcast.EventConsumer;

import java.util.List;
import java.util.Optional;

public interface Player extends EventConsumer {

    Team team();

    Player team(Team team);

    List<Card> hand();

    Player clearHand();

    Player addCards(List<Card> cards);

    Optional<Color> chooseTrump(Card card, boolean firstRun);

    Card play(Trick trick);
}
