package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.broadcast.Broadcast;
import fr.rossi.belote.core.domain.event.GameEnd;
import fr.rossi.belote.core.domain.event.GameStarted;
import fr.rossi.belote.core.domain.event.RoundEnd;
import fr.rossi.belote.core.domain.event.StartRound;
import fr.rossi.belote.core.exception.ActionTimeoutException;
import fr.rossi.belote.core.output.LogBroadcast;
import fr.rossi.belote.core.utils.Params;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static fr.rossi.belote.core.exception.TechnicalException.assertEquals;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class Game {

    private final String id;
    private final Broadcast broadcast;
    private final PlayersHandler players;
    private final int scoreMax;
    private final Map<Team, Integer> scores;
    private List<Card> cards;

    public Game(List<Team> teams, int scoreMax) {
        super();
        this.id = UUID.randomUUID().toString();
        this.broadcast = new Broadcast(new LogBroadcast());
        assertEquals("Error in the number of teams", Params.NB_TEAMS, teams.size());
        this.players = new PlayersHandler(teams);
        this.players.players().forEach(player -> player.gameId(this.id));
        this.players.players().forEach(broadcast::subscribe);
        this.scoreMax = scoreMax;

        this.scores = this.players.teams().stream().collect(toMap(identity(), t -> 0));
        this.cards = new ArrayList<>(Card.getCards());
        this.broadcast.consume(new GameStarted(this.players.players()));
    }

    public Game(List<Team> teams) {
        this(teams, 1_000);
    }

    public Team play() {
        Team winner;
        GameEnd.Status status;
        try {
            winner = run();
            status = GameEnd.Status.SIMPLE;
        } catch (ActionTimeoutException e) {
            log.warn(e.getMessage());
            var timeoutPlayer = this.players.playerByName(e.getPlayerName()).orElseThrow();
            winner = this.players.otherTeam(timeoutPlayer.team());
            status = GameEnd.Status.TIMEOUT;
        }

        this.broadcast.consume(new GameEnd(winner, status, this.scores));
        return winner;
    }

    private Team run() throws ActionTimeoutException {
        Optional<Team> winner;
        do {
            this.broadcast.consume(new StartRound(this.players.players().get(0)));
            var dealer = new Dealer(this.broadcast, this.players.players(), this.cards);
            var deal = dealer.deal();
            if (deal.isPresent()) this.runRound(deal.get());
            this.cards = dealer.collectCards();

            winner = this.getWinner();
            this.players.next();
        } while (winner.isEmpty());
        return winner.get();
    }

    private void runRound(TrumpParams trumpParams) throws ActionTimeoutException {
        final RoundEnd roundEnd = new Round(this.broadcast, players,
                trumpParams.color(), trumpParams.player().team()).run();
        // TODO Handle output cards

        roundEnd.runScores().forEach((team, score) -> this.scores
                .compute(team, (t, s) -> (s == null ? 0 : s) + score));
        this.broadcast.consume(roundEnd.scores(new HashMap<>(this.scores)));
    }

    private Optional<Team> getWinner() {
        return this.scores.entrySet().stream()
                .filter(e -> e.getValue() >= scoreMax)
                .max((e1, e2) -> -Integer.compare(e1.getValue(), e2.getValue()))
                .map(Map.Entry::getKey);
    }
}
