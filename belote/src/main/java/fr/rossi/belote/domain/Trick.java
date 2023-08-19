package fr.rossi.belote.domain;

import fr.rossi.belote.card.Card;

import java.util.Collection;
import java.util.List;

public interface Trick {

    List<Card> cards();

    CardAndPlayer winner();

    Collection<Card> playableCards(Collection<Card> hand);

    boolean isPartnerLeader();
}
