package fr.rossi.belote.core.player.brain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.CardAndPlayer;
import fr.rossi.belote.core.domain.CardsAndPlayers;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import fr.rossi.belote.core.utils.Reducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
public class PlayersSimulator {

    private final List<Player> players;
    private final Player player;
    private final Collection<Card> playerHand;
    private final TrumpChosen trumpChosen;
    private final CardsAndPlayers playedCards;
    private final Map<Player, PlayerSimulator> simulators;

    public PlayersSimulator(List<Player> players, Player player, Collection<Card> playerHand, TrumpChosen trumpChosen) {
        this(players, player, playerHand, trumpChosen, new CardsAndPlayers(),
                players.stream()
                        .map(p -> new PlayerSimulator(p, player, playerHand, trumpChosen))
                        .collect(toMap(PlayerSimulator::getPlayer, identity())));
    }

    private PlayersSimulator(PlayersSimulator source, CardsAndPlayers playedCards, Map<Player, PlayerSimulator> simulators) {
        this(source.players, source.player, source.playerHand, source.trumpChosen, playedCards, simulators);
    }

    private Map<Player, PlayerSimulator> updateSimulators(CardsAndPlayers trick) {
        var newSimulators = this.simulators.values().stream()
                .map(ps -> new PlayerSimulator(ps, trick))
                .toList();

        // Use the knownHand to remove from others potentialHand
        while (Reducer.runAllAndReduce(newSimulators, simulator -> simulator.learnFromOthers(newSimulators))) ;
        return newSimulators.stream().collect(toMap(PlayerSimulator::getPlayer, identity()));
    }

    public PlayersSimulator trickEnd(CardsAndPlayers cards) {
        return new PlayersSimulator(this, this.playedCards.add(cards), this.updateSimulators(cards));
    }

    public void print(Trick currentTrick) {
        log.info(this.updateSimulators(currentTrick.cardsAndPlayers()).values().stream()
                .map(PlayerSimulator::toString)
                .collect(joining(System.lineSeparator())));
    }

    public Collection<Card> potentialHand(Player player) {
        return this.simulators.get(player).getPotentialHand();
    }

    public Collection<? extends Collection<Card>> potentialHands(Player player, CardsAndPlayers currentTrick) {
        return new PlayerSimulator(this.simulators.get(player), currentTrick).potentialHands();
    }

    public boolean hasBeenPlayed(Card card) {
        return this.playedCards.stream().map(CardAndPlayer::card).anyMatch(card::equals);
    }

    public boolean couldHave(Player player, Card card) {
        return this.simulators.get(player).getPotentialHand().contains(card);
    }
}
