package fr.rossi.belote.core.player.brain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Figure;
import fr.rossi.belote.core.domain.*;
import fr.rossi.belote.core.domain.event.TrickEnd;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import fr.rossi.belote.core.exception.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class TrickSimulator {

    private final PlayersSimulator simulator;
    private final TrumpChosen trumpChosen;
    private final Collection<Team> teams;
    private final Player player;
    private Optional<Card> choose;
    private boolean partnerShouldWin;

    private static boolean sure(double value) {
        return value > 0.999;
    }

    private static boolean hasPlayed(Trick trick, Player player) {
        return trick.cardsAndPlayers().stream().map(CardAndPlayer::player).anyMatch(player::equals);
    }

    public void trickEnd(TrickEnd e) {
        var teamWin = e.winner().team().equals(this.player.team());
        if (teamWin) return;
        this.choose.ifPresent(c -> {
            // throw new TechnicalException("Brain choose: (" + this.player + " " + c + "): " + e.cards());
        });
        if (this.partnerShouldWin)
            throw new TechnicalException("Partner should win!");
    }

    public Optional<Card> chooseCard(Trick trick) {
        this.choose = simulator.potentialHands(player, trick.cardsAndPlayers())
                .stream()
                .map(hand -> trick.playableCards(player, hand))
                .map(card -> this.chooseCard(trick, card))
                // TODO Reduce
                .reduce(CollectionUtils::intersection)
                .orElseGet(Collections::emptyList)
                .stream().findAny();
        return this.choose;
    }

    private boolean hasTrump() {
        return this.trumpChosen.hasChoose(this.player.team());
    }

    private Collection<Card> chooseCard(Trick trick, Collection<Card> playableCards) {
        if (hasPlayed(trick, this.player.partner())) {
            if (sure(this.winProb(trick, this.player.partner()))) {
                // TODO Will be won by our partner
                this.partnerShouldWin = true;
                return List.of(playableCards.stream().min(Comparator.naturalOrder()).orElseThrow());
            }
        }

        var trumpFirst = this.trumpFirst(trick, playableCards);
        if (trumpFirst.isPresent()) return trumpFirst.get();

        var cards = playableCards.stream()
                .filter(card -> this.hasTrump() || card.color() != this.trumpChosen.chosenColor())
                .filter(card -> sure(this.winProb(trick, this.player, card)))
                .toList();
        if (!cards.isEmpty()) return cards;
        return Collections.emptyList();
    }

    private Optional<Collection<Card>> trumpFirst(Trick trick, Collection<Card> playableCards) {
        if (!trick.cardsAndPlayers().isEmpty() || !this.hasTrump()) return Optional.empty();

        // Play 20 first
        var twenty = new Card(Figure.VALET, this.trumpChosen.chosenColor());
        var twentyPlayed = this.simulator.hasBeenPlayed(twenty);
        if (playableCards.contains(twenty)) return Optional.of(List.of(twenty));

        // If twenty have been played, play 14
        var fourteen = new Card(Figure.NEUF, this.trumpChosen.chosenColor());
        if (twentyPlayed && playableCards.contains(fourteen)) return Optional.of(List.of(fourteen));

        if (this.trumpChosen.player().equals(this.player)
                || !this.simulator.couldHave(this.player.partner(), twenty)) return Optional.empty();

        // Partner has choose: guess partner has twenty
        playableCards.removeIf(card -> !this.trumpChosen.chosenColor().has(card));
        playableCards.removeAll(List.of(twenty, fourteen));
        return playableCards.stream()
                .filter(this.trumpChosen.chosenColor()::has)
                .filter(card -> !twenty.equals(card) && !fourteen.equals(card))
                .sorted().findFirst().map(List::of);
    }

    private double winProb(Trick trick, Player player) {
        var card = trick.card(player).orElseThrow();
        if (!trick.winner().player().equals(player)) return 0;
        if (this.isStrongest(trick, player, card)) return 1;
        return 0.5;
    }

    private double winProb(Trick trick, Player player, Card card) {
        var newTrick = trick.addCard(player, card);
        if (!newTrick.winner().player().equals(player)) return 0;
        if (this.isStrongest(trick, player, card)) return 1;
        return 0.5;
    }

    private boolean isStrongest(Trick trick, Player player, Card card) {
        var alreadyPlayed = trick.cardsAndPlayers().stream().map(CardAndPlayer::player).toList();
        return this.teams.stream()
                .filter(team -> !player.team().equals(team))
                .map(Team::players).flatMap(Collection::stream)
                .filter(p -> !alreadyPlayed.contains(p))
                .map(this.simulator::potentialHand).flatMap(Collection::stream)
                .distinct()
                .noneMatch(c -> !this.isStronger(trick, card, c));
    }

    private Boolean isStronger(Trick trick, Card card, Card c) {
        var cards = new ArrayList<>(trick.cardsAndPlayers().stream().map(CardAndPlayer::card).toList());
        if (!cards.contains(card)) cards.add(card);
        cards.add(c);
        return CardsAndPlayers.getWinner(cards, this.trumpChosen.chosenColor())
                .map(card::equals).orElse(false);
    }
}
