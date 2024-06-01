package fr.rossi.belote.core.player.brain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.event.*;
import fr.rossi.belote.core.exception.ActionTimeoutException;
import fr.rossi.belote.core.player.SimplePlayer;
import fr.rossi.belote.core.player.brain.trumpchoice.TrumpChoice;
import fr.rossi.belote.core.player.brain.trumpchoice.TrumpChooser;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class BrainPlayer extends SimplePlayer {

    private List<Player> players;
    private TrumpChosen trumpChosen;
    private PlayersSimulator simulator;
    private TrickSimulator trickSimulator;
    private TrumpChoice trumpChoice;

    public BrainPlayer(String name) {
        super(name);
    }

    @Override
    public void consume(Event event) {
        switch (event) {
            case GameStarted e -> this.players = e.players();
            case TrumpChosen e -> this.trumpChosen = e;
            case RoundStarted _ -> this.simulator = new PlayersSimulator(
                    this.players, this, this.hand(), this.trumpChosen);
            case TrickEnd e -> {
                if (this.trickSimulator != null) this.trickSimulator.trickEnd(e);
                this.simulator = this.simulator.trickEnd(e.cards());
            }
            case RoundEnd e -> {
                if (this.trumpChoice != null) this.trumpChoice.feedback(e.runScores().get(this.team()));
                this.trumpChoice = null;
            }
            default -> log.debug("Not handled event: {}", event);
        }
    }

    @Override
    protected Card cardToPlay(Trick trick, Collection<Card> playableCards) throws ActionTimeoutException {
        var teams = this.players.stream().map(Player::team).distinct().toList();
        // TODO Check if win when choose card (vs random)
        this.trickSimulator = new TrickSimulator(this.simulator.trickEnd(trick.cardsAndPlayers()), this.trumpChosen, teams, this);
        var chosenCard = this.trickSimulator.chooseCard(trick);
        return chosenCard.isPresent()
                ? chosenCard.get()
                : super.cardToPlay(trick, playableCards);
    }

    @Override
    public Optional<Color> chooseTrump(Card card, boolean firstRun) {
        var counter = new TrumpChooser(this.hand(), card);
        // TODO Parameter
        var minConfidence = 0.5;
        var choice = firstRun
                ? counter.choose(card.color(), minConfidence)
                : Arrays.stream(Color.values())
                .filter(color -> color != card.color())
                .map(c -> counter.choose(c, minConfidence))
                .filter(Optional::isPresent).map(Optional::get)
                .max(Comparator.comparingInt(TrumpChoice::handPoints));
        this.trumpChoice = choice.orElse(null);
        return choice.map(TrumpChoice::color);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
