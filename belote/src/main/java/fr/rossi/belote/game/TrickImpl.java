package fr.rossi.belote.game;

import fr.rossi.belote.Params;
import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.domain.CardAndPlayer;
import fr.rossi.belote.domain.Player;
import fr.rossi.belote.domain.Trick;
import lombok.extern.java.Log;

import java.util.*;

import static fr.rossi.belote.exception.TechnicalException.*;

@Log
public class TrickImpl implements Trick {

    private final Color trumpColor;
    private final List<Card> cards;
    private final Map<Card, Player> cardsAndPlayers;
    private Color wantedColor;

    TrickImpl(Color trumpColor) {
        super();
        this.trumpColor = trumpColor;
        this.cards = new ArrayList<>();
        this.cardsAndPlayers = new HashMap<>();
    }

    @Override
    public List<Card> cards() {
        return this.cards;
    }

    Player run(List<Player> players) {
        for (Player player : players) {
            final Card card = player.play(this);
            log.info(player + " play " + card);
            this.addCard(player, card);
        }

        return this.winner().player();
    }

    int getPoints() {
        return this.cards.stream().mapToInt(card -> card.getPoints(this.trumpColor)).sum();
    }

    void addCard(Player player, Card card) {

        assertLower("Trick cards nb should be lower than nb players", this.cards.size(), Params.NB_PLAYERS);
        if (this.cards.isEmpty()) {
            this.wantedColor = card.color();
        }
        this.cards.add(card);
        this.cardsAndPlayers.put(card, player);
    }

    @Override
    public CardAndPlayer winner() {
        assertFalse("Can't get winner if cards is empty", this.cards.isEmpty());
        var card = this.getWinnerForColor(this.trumpColor)
                .orElseGet(() -> this.getWinnerForColor(this.wantedColor).orElseThrow());
        return new CardAndPlayer(card, this.cardsAndPlayers.get(card));
    }

    private Optional<Card> getWinnerForColor(Color color) {
        return this.cards.stream()
                .filter(color::has)
                .min((c1, c2) -> c1.figure().compareTo(c2.figure(), color == this.trumpColor));
    }

    @Override
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
            return this.wantedColor == this.trumpColor
                    ? this.filterTrumpUpperIsPossible(wantedColorCards)
                    : wantedColorCards;
        }

        final Collection<Card> handTrumpCards = this.trumpColor.filter(hand);
        if (handTrumpCards.isEmpty()) {
            // Player have nor wanted color nor trump could play all his cards
            return hand;
        }

        final Collection<Card> playedTrumpCards = this.trumpColor.filter(this.cards);
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
        final Card leader = this.winner().card();
        assertEquals(String.format("Leader card should be trump (%s)", this.cardsAndPlayers), this.trumpColor, leader.color());

        final List<Card> upper = handTrumpCards.stream()
                .filter(card -> card.figure().compareTo(leader.figure(), true) < 0)
                .toList();
        return upper.isEmpty() ? handTrumpCards : upper;
    }

    @Override
    public boolean isPartnerLeader() {
        return this.cards.size() > 1 && this.cards.get(this.cards.size() - Params.NB_TEAMS).equals(this.winner().card());
    }

    Optional<Player> player(Card card) {
        return Optional.ofNullable(this.cardsAndPlayers.get(card));
    }
}
