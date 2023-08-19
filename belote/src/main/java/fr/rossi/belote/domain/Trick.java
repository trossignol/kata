package fr.rossi.belote.domain;

import fr.rossi.belote.card.Card;

import java.util.Collection;

public interface Trick {

    CardsAndPlayers cardsAndPlayers();

    CardAndPlayer winner();

    Collection<Card> playableCards(Player player);

    boolean isPartnerLeader(Player player);
}
