package fr.rossi.belote.game;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Trick {

    private final Round round;
    private final List<Card> cards;
    private Color wantedColor;

    Trick(Round round) {
        super();
        this.round = round;
        this.cards = new ArrayList<>();
    }

    private Color trump() {
        return this.round.trump();
    }

    public void addCard(Card card) {
        if (this.cards.isEmpty()) {
            this.wantedColor = card.color();
        }
        this.cards.add(card);
    }

    public Card getWinner() {
        assert !this.cards.isEmpty();
        return this.getWinnerForColor(this.trump())
                .orElseGet(() -> this.getWinnerForColor(this.wantedColor).orElseThrow());
    }

    private Optional<Card> getWinnerForColor(Color color) {
        return this.cards.stream()
                .filter(color::has)
                .min((c1, c2) -> c1.figure().compareTo(c2.figure(), color == this.trump()));
    }

    public Collection<Card> playableCards(Collection<Card> hand) {
        if (this.cards.isEmpty()) {
            // No limit for first player
            return hand;
        }

        final Collection<Card> wantedColorCards = this.wantedColor.filter(hand);
        if (!wantedColorCards.isEmpty()) {
            // Player have wanted color:
            // - if it's trump should go upper
            // - else could play any wanted color card
            return this.wantedColor == this.trump()
                    ? this.filterTrumpUpperIsPossible(wantedColorCards)
                    : wantedColorCards;
        }

        final Collection<Card> handTrumpCards = this.trump().filter(hand);
        if (handTrumpCards.isEmpty()) {
            // Player have nor wanted color nor trump could play all his cards
            return hand;
        }

        final Collection<Card> playedTrumpCards = this.trump().filter(this.cards);
        if (playedTrumpCards.isEmpty()) {
            // No other trump cards:
            // - if partner is leader, player can play any card
            // - else player should play trump
            return this.isPartnerLeader() ? hand : handTrumpCards;
        }

        // Other trump cards: player should go upper
        return this.filterTrumpUpperIsPossible(handTrumpCards);

    }

    private Collection<Card> filterTrumpUpperIsPossible(Collection<Card> handTrumpCards) {
        final Card leader = this.getWinner();
        assert leader.color() == this.trump();

        final List<Card> upper = handTrumpCards.stream()
                .filter(card -> card.figure().compareTo(leader.figure(), true) < 0)
                .toList();
        return upper.isEmpty() ? handTrumpCards : upper;
    }

    private boolean isPartnerLeader() {
        return this.cards.size() > 1 && this.cards.get(this.cards.size() - 2).equals(this.getWinner());

    }
}
