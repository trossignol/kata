package fr.rossi.belote.core.player.brain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.CardAndPlayer;
import fr.rossi.belote.core.domain.CardsAndPlayers;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import fr.rossi.belote.core.utils.Combination;
import fr.rossi.belote.core.utils.Reducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static fr.rossi.belote.core.card.Card.printHand;
import static fr.rossi.belote.core.exception.TechnicalException.assertEquals;
import static fr.rossi.belote.core.exception.TechnicalException.assertTrue;

@Slf4j
public class PlayerSimulator {

    @Getter
    private final Player player;
    private final int nbCardsInHand;
    private final Color trumpColor;
    @Getter
    private final Set<Card> knownHand;
    @Getter
    private final Set<Card> potentialHand;

    PlayerSimulator(Player player, Player me, Collection<Card> myHand, TrumpChosen trumpChosen) {
        super();
        this.player = player;
        this.nbCardsInHand = myHand.size();
        this.trumpColor = trumpChosen.chosenColor();

        if (player.equals(me)) {
            this.knownHand = new HashSet<>(myHand);
            this.potentialHand = new HashSet<>(myHand);
            return;
        }

        this.knownHand = new HashSet<>();
        this.potentialHand = new HashSet<>(Card.getCards());

        this.potentialHand.removeAll(myHand);
        if (!this.player.equals(trumpChosen.player())) {
            this.potentialHand.remove(trumpChosen.card());
        } else {
            this.knownHand.add(trumpChosen.card());
        }

        this.updateKnownFromPotential();
    }

    PlayerSimulator(PlayerSimulator source, CardsAndPlayers trick) {
        super();
        this.player = source.player;
        this.nbCardsInHand = source.nbCardsInHand - (trick.hasPlayer(this.player) ? 1 : 0);
        this.trumpColor = source.trumpColor;

        this.knownHand = new HashSet<>(source.knownHand);
        this.potentialHand = new HashSet<>(source.potentialHand);

        if (trick.isEmpty()) return;

        trick.stream()
                .filter(cp -> this.knownHand.contains(cp.card()))
                .forEach(cp -> {
                    assertTrue(
                            String.format("Error in known hand for %s: %s has been played by %s", this.player, cp.card(), cp.player()),
                            this.player.equals(cp.player()));
                    this.knownHand.remove(cp.card());
                });

        trick.stream()
                .filter(cp -> this.player.equals(cp.player()))
                .map(CardAndPlayer::card)
                .findAny()
                .ifPresent(playedCard -> this.learnFromPlayedCard(trick, playedCard));

        trick.forEach(cp -> this.potentialHand.remove(cp.card()));

        this.updateKnownFromPotential();
    }

    private void updateKnownFromPotential() {
        if (this.potentialHand.size() != this.nbCardsInHand) return;

        var errorCards = this.knownHand.stream().filter(card -> !this.potentialHand.contains(card)).toList();
        assertTrue(
                String.format("Known cards but not in potential hand: player=%s cards=%s hand=%s", this.player, errorCards, this.potentialHand),
                errorCards.isEmpty());
        this.knownHand.addAll(this.potentialHand);
        assertEquals(
                String.format("Error for %s knowHand=%s", this.player, this.knownHand),
                this.knownHand.size(), this.nbCardsInHand);
    }

    private void learnFromPlayedCard(CardsAndPlayers trick, Card playedCard) {
        assertTrue(
                String.format("Unexpected card %s for player %s (%s)", playedCard, this.player, this.potentialHand),
                this.potentialHand.contains(playedCard));

        // First player: nothing to learn
        if (trick.size() == 1) return;

        // Played the wanted color: nothing to learn
        var wantedColor = trick.wantedColor().orElseThrow();
        if (playedCard.color() == wantedColor) return;

        // Played another color: no wanted color anymore
        this.potentialHand.removeIf(card -> card.color() == wantedColor);

        // TODO Search for the leader !before us!
        var trickBeforePlaying = new CardsAndPlayers();
        for (var cp : trick) {
            if (this.player.equals(cp.player())) break;
            trickBeforePlaying = trickBeforePlaying.add(cp.card(), cp.player());
        }

        var winner = trickBeforePlaying.getWinner(this.trumpColor).orElseThrow();

        // Partner leader => Nothing more to learn
        if (this.player.team().equals(winner.player().team())) return;

        // Opponent leader
        // playedCard is not trump => No more trump
        if (playedCard.color() != this.trumpColor) {
            this.potentialHand.removeIf(card -> card.color() == this.trumpColor);
            return;
        }

        // No trump played => Nothing more to learn
        if (winner.card().color() != this.trumpColor) return;

        log.info("{}: {} - {}: {}", this.player, playedCard, winner.player(), winner.card());
        if (playedCard.figure().compareTo(winner.card().figure(), true) > 0) {
            // playedCard is lower than trump => No more upper trump
            this.potentialHand.removeIf(card -> card.color() == trumpColor
                    && (card.figure() == winner.card().figure()
                    || (card.figure().compareTo(winner.card().figure(), true) < 0)));
        }
        // playedCard is upper than trump => Nothing more to learn
    }

    boolean learnFromOthers(Collection<PlayerSimulator> simulators) {
        var change = Reducer.runAllAndReduce(simulators, simulator -> {
            if (this.player.equals(simulator.player)) return false;
            if (simulator.knownHand.stream().noneMatch(this.potentialHand::contains)) return false;
            this.potentialHand.removeAll(simulator.knownHand);
            return true;
        });

        if (!change) return false;
        this.updateKnownFromPotential();
        return true;
    }

    public String toString() {
        var str = String.format("%s (%d) known=%s", this.player, this.nbCardsInHand, this.knownHand);
        if (this.knownHand.size() < this.nbCardsInHand)
            str += System.lineSeparator() + printHand(this.potentialHand);
        return str;
    }

    int nbCardsInHand() {
        return this.nbCardsInHand;
    }


    public Collection<? extends Collection<Card>> potentialHands() {
        if (this.nbCardsInHand == this.knownHand.size()) return List.of(this.knownHand);
        var combinations = Combination.generate(
                this.potentialHand.stream().filter(card -> !this.knownHand.contains(card)).toList(),
                this.nbCardsInHand - this.knownHand.size());

        if (this.knownHand.isEmpty()) return combinations;

        return combinations.stream()
                .map(combination -> {
                    var list = new ArrayList<>(combination);
                    list.addAll(this.knownHand);
                    return list;
                }).toList();
    }
}
