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
import java.util.Optional;

import static fr.rossi.belote.core.exception.TechnicalException.*;

@Slf4j
public class TrickImpl implements Trick {

    private final Color trumpColor;
    private final CardsAndPlayers cardsAndPlayers;
    private Color wantedColor;

    TrickImpl(Color trumpColor) {
        super();
        this.trumpColor = trumpColor;
        this.cardsAndPlayers = new CardsAndPlayers();
    }

    Player run(List<Player> players) {
        for (Player player : players) {
            var card = player.play(this);
            log.debug("{} play {}", player, card);
            this.addCard(player, card);
        }

        return this.winner().player();
    }

    @Override
    public CardsAndPlayers cardsAndPlayers() {
        return this.cardsAndPlayers;
    }

    int getPoints() {
        return this.cardsAndPlayers.stream()
                .map(CardAndPlayer::card)
                .mapToInt(card -> card.getPoints(this.trumpColor)).sum();
    }

    void addCard(Player player, Card card) {
        assertLower("Trick cards nb should be lower than nb players", this.cardsAndPlayers.size(), Params.NB_PLAYERS);
        if (this.cardsAndPlayers.isEmpty()) {
            this.wantedColor = card.color();
        }
        this.cardsAndPlayers.add(new CardAndPlayer(card, player));
    }

    @Override
    public CardAndPlayer winner() {
        assertFalse("Can't get winner if cards is empty", this.cardsAndPlayers.isEmpty());
        return this.getWinnerForColor(this.trumpColor)
                .orElseGet(() -> this.getWinnerForColor(this.wantedColor).orElseThrow());
    }

    private Optional<CardAndPlayer> getWinnerForColor(Color color) {
        return this.cardsAndPlayers.stream()
                .filter(c -> color.has(c.card()))
                .min((c1, c2) -> c1.card().figure().compareTo(c2.card().figure(), color == this.trumpColor));
    }

    @Override
    public Collection<Card> playableCards(Player player) {
        if (this.cardsAndPlayers.isEmpty()) {
            // No limit for first player
            return player.hand();
        }

        final Collection<Card> wantedColorCards = this.wantedColor.filter(player.hand());
        if (!wantedColorCards.isEmpty()) {
            // Player have wanted color:
            // - if it's trump should go upper
            // - else could play any wanted color card
            return this.wantedColor == this.trumpColor
                    ? this.filterTrumpUpperIfPossible(wantedColorCards)
                    : wantedColorCards;
        }

        final Collection<Card> handTrumpCards = this.trumpColor.filter(player.hand());
        if (handTrumpCards.isEmpty()) {
            // Player have nor wanted color nor trump could play all his cards
            return player.hand();
        }

        final Collection<Card> playedTrumpCards = this.trumpColor.filter(
                this.cardsAndPlayers.stream().map(CardAndPlayer::card).toList());
        if (playedTrumpCards.isEmpty()) {
            // No other trump cards:
            // - if partner is leader, player can play any card
            // - else player should play trump
            return this.isPartnerLeader(player) ? player.hand() : handTrumpCards;
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

    @Override
    public boolean isPartnerLeader(Player player) {
        return !this.cardsAndPlayers.isEmpty() && this.winner().player().team().equals(player.team());
    }

    Optional<Player> player(Card card) {
        return this.cardsAndPlayers.stream()
                .filter(c -> c.card().equals(card))
                .findAny().map(CardAndPlayer::player);
    }
}
