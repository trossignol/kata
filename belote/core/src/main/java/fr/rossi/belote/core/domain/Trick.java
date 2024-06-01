package fr.rossi.belote.core.domain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;

import java.util.Collection;
import java.util.Optional;

public interface Trick {

    Color trumpColor();

    CardsAndPlayers cardsAndPlayers();

    CardAndPlayer winner();

    Collection<Card> playableCards(Player player, Collection<Card> hand);

    Trick addCard(Player player, Card card);

    default Optional<Player> player(Card card) {
        return this.cardsAndPlayers().stream()
                .filter(c -> c.card().equals(card))
                .findAny().map(CardAndPlayer::player);
    }

    default Optional<Card> card(Player player) {
        return this.cardsAndPlayers().stream()
                .filter(c -> c.player().equals(player))
                .findAny().map(CardAndPlayer::card);
    }

    default int getPoints() {
        return this.cardsAndPlayers().stream()
                .map(CardAndPlayer::card)
                .mapToInt(card -> card.getPoints(this.trumpColor())).sum();
    }
}
