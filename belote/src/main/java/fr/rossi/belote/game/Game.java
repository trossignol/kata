package fr.rossi.belote.game;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.domain.Team;
import fr.rossi.belote.domain.broadcast.Broadcast;
import fr.rossi.belote.domain.event.RoundEnd;
import fr.rossi.belote.output.LogBroadcast;
import fr.rossi.belote.utils.Params;

import java.util.*;

import static fr.rossi.belote.exception.TechnicalException.assertEquals;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class Game {

    private final Broadcast broadcast;
    private final PlayersHandler players;
    private final int scoreMax;
    private final Map<Team, Integer> scores;
    private List<Card> cards;

    public Game(List<Team> teams, int scoreMax) {
        super();
        this.broadcast = new Broadcast(new LogBroadcast());
        assertEquals("Error in the number of teams", Params.NB_TEAMS, teams.size());
        this.players = new PlayersHandler(teams);
        this.players.players().forEach(broadcast::subscribe);
        this.scoreMax = scoreMax;

        this.scores = this.players.teams().stream().collect(toMap(identity(), t -> 0));
        this.cards = new ArrayList<>(Card.getCards());
    }

    public Game(List<Team> teams) {
        this(teams, 1_000);
    }

    public Team play() {
        Optional<Team> winner;
        do {
            var dealer = new Dealer(this.broadcast, this.players.players(), this.cards);
            dealer.deal().ifPresent(this::runRound);
            this.cards = dealer.collectCards();

            winner = this.getWinner();
            this.players.next();
        } while (winner.isEmpty());
        return winner.get();
    }

    private void runRound(TrumpParams trumpParams) {
        final RoundEnd roundEnd = new Round(this.broadcast, players,
                trumpParams.color(), trumpParams.player().team()).run();
        // TODO Handle output cards

        roundEnd.runScores().forEach((team, score) -> this.scores
                .compute(team, (t, s) -> s + score));
        this.broadcast.consume(roundEnd.scores(new HashMap<>(this.scores)));
    }

    private Optional<Team> getWinner() {
        return this.scores.entrySet().stream()
                .filter(e -> e.getValue() >= scoreMax)
                .max((e1, e2) -> -Integer.compare(e1.getValue(), e2.getValue()))
                .map(Map.Entry::getKey);
    }
}
