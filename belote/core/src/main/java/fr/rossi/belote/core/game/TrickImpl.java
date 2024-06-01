package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.CardAndPlayer;
import fr.rossi.belote.core.domain.CardsAndPlayers;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.utils.Params;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

import static fr.rossi.belote.core.exception.TechnicalException.*;

@Slf4j
public class TrickImpl implements Trick {

    private final Color trumpColor;
    private final CardsAndPlayers cardsAndPlayers;
    private CardAndPlayer winner;

    public TrickImpl(Color trumpColor) {
        super();
        this.trumpColor = trumpColor;
        this.cardsAndPlayers = new CardsAndPlayers();
    }

    private TrickImpl(Trick source, Player player, Card card) {
        super();
        this.trumpColor = source.trumpColor();
        this.cardsAndPlayers = source.cardsAndPlayers().add(card, player);
    }

    @Override
    public Color trumpColor() {
        return this.trumpColor;
    }

    @Override
    public CardsAndPlayers cardsAndPlayers() {
        return this.cardsAndPlayers;
    }

    @Override
    public TrickImpl addCard(Player player, Card card) {
        assertLower("Trick cards nb should be lower than nb players", this.cardsAndPlayers.size(), Params.NB_PLAYERS);
        return new TrickImpl(this, player, card);
    }

    @Override
    public CardAndPlayer winner() {
        if (this.winner == null) {
            assertFalse("Can't get winner if cards is empty", this.cardsAndPlayers.isEmpty());
            this.winner = this.cardsAndPlayers.getWinner(this.trumpColor).orElseThrow();
        }
        return this.winner;
    }

    @Override
    public Collection<Card> playableCards(Player player, Collection<Card> hand) {
        if (this.cardsAndPlayers.isEmpty()) {
            // No limit for first player
            return hand;
        }

        var wantedColor = this.cardsAndPlayers.wantedColor().orElseThrow();
        final Collection<Card> wantedColorCards = wantedColor.filter(hand);
        if (!wantedColorCards.isEmpty()) {
            // Player have wanted color:
            // - if it's trump should go upper
            // - else could play any wanted color card
            return wantedColor == this.trumpColor
                    ? this.filterTrumpUpperIfPossible(wantedColorCards)
                    : wantedColorCards;
        }

        final Collection<Card> handTrumpCards = this.trumpColor.filter(hand);
        if (handTrumpCards.isEmpty()) {
            // Player have nor wanted color nor trump could play all his cards
            return hand;
        }

        final Collection<Card> playedTrumpCards = this.trumpColor.filter(
                this.cardsAndPlayers.stream().map(CardAndPlayer::card).toList());
        if (playedTrumpCards.isEmpty()) {
            // No other trump cards:
            // - if partner is leader, player can play any card
            // - else player should play trump
            var partnerLeader = !this.cardsAndPlayers.isEmpty()
                    && this.winner().player().team().equals(player.team());
            return partnerLeader ? hand : handTrumpCards;
        }

        // Other trump cards: player should go upper
        return this.filterTrumpUpperIfPossible(handTrumpCards);

    }

    private Collection<Card> filterTrumpUpperIfPossible(Collection<Card> handTrumpCards) {
        final Card leader = this.winner().card();
        assertEquals(String.format("Leader card should be trump (%s)", this.cardsAndPlayers), this.trumpColor, leader.color());

        final List<Card> upper = handTrumpCards.stream()
                .filter(card -> card.figure().compareTo(leader.figure(), true) < 0)
                .toList();
        return upper.isEmpty() ? handTrumpCards : upper;
    }
}
