package fr.rossi.belote.domain;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;

import java.util.List;
import java.util.Optional;

public interface Player {

    Team team();

    void team(Team team);

    List<Card> hand();

    void clearHand();

    void addCards(List<Card> cards);

    Optional<Color> chooseTrump(Card card, boolean firstRun);

    Card play(Trick trick);
}
