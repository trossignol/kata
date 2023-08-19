package fr.rossi.belote.game;

import fr.rossi.belote.Params;
import fr.rossi.belote.card.Card;
import fr.rossi.belote.domain.Team;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.rossi.belote.exception.TechnicalException.assertEquals;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Log
public class Game {

    private final PlayersHandler players;
    private final int scoreMax;
    private final Map<Team, Integer> scores;
    private List<Card> cards;

    public Game(List<Team> teams, int scoreMax) {
        super();
        assertEquals("Error in the number of teams", Params.NB_TEAMS, teams.size());
        this.players = new PlayersHandler(teams);
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
            var dealer = new Dealer(this.players.players(), this.cards);
            dealer.deal().ifPresent(this::runRound);
            this.cards = dealer.collectCards();

            winner = this.getWinner();
            this.players.next();
        } while (winner.isEmpty());
        return winner.get();
    }

    private void runRound(TrumpParams trumpParams) {
        new Round(players, trumpParams.color(), trumpParams.player().team())
                .run()
                // TODO Handle output cards
                .forEach((team, score) -> this.scores.compute(team, (t, s) -> s + score));
    }

    private Optional<Team> getWinner() {
        this.scores.forEach((team, score) -> log.info(">>> " + team + ": score=" + score));
        return this.scores.entrySet().stream()
                .filter(e -> e.getValue() >= scoreMax)
                .max((e1, e2) -> -Integer.compare(e1.getValue(), e2.getValue()))
                .map(Map.Entry::getKey);
    }
}
