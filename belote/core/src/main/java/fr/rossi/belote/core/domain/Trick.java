package fr.rossi.belote.core.domain;

import fr.rossi.belote.core.card.Card;

import java.util.Collection;

public interface Trick {

    CardsAndPlayers cardsAndPlayers();

    CardAndPlayer winner();

    Collection<Card> playableCards(Player player);

    boolean isPartnerLeader(Player player);
}
