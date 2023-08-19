package fr.rossi.belote.game;

import fr.rossi.belote.Params;
import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.card.Figure;
import fr.rossi.belote.domain.Player;
import fr.rossi.belote.domain.Team;
import fr.rossi.belote.score.ScoreCalculator;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.rossi.belote.exception.TechnicalException.assertNotNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Log
public class Round {

    private static final int NB_TRICKS = Card.nbCards() / Params.NB_PLAYERS;

    private final PlayersHandler players;
    private final Color trumpColor;
    private final Team trumpTeam;
    private final Map<Team, List<TrickImpl>> wonTricks;

    Round(PlayersHandler players, Color trumpColor, Team trumpTeam) {
        super();
        this.players = players;
        this.trumpColor = trumpColor;
        this.trumpTeam = trumpTeam;
        this.wonTricks = this.players.teams().stream()
                .collect(toMap(identity(), t -> new ArrayList<>(NB_TRICKS)));
    }

    public Map<Team, Integer> run() {
        Player winnerPlayer = null;
        var currentPlayers = this.players;

        for (int i = 0; i < NB_TRICKS; i++) {
            var trick = new TrickImpl(this.trumpColor);
            winnerPlayer = trick.run(currentPlayers.players());
            this.wonTricks.get(winnerPlayer.team()).add(trick);
            currentPlayers = currentPlayers.next(winnerPlayer);
        }
        assertNotNull("No winner player found", winnerPlayer);
        return scoresForTricks(Optional.ofNullable(winnerPlayer).orElseThrow().team());
    }

    private Map<Team, Integer> scoresForTricks(Team lastWinner) {
        var scores = this.wonTricks.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        e -> e.getValue().stream().mapToInt(TrickImpl::getPoints).sum()));

        return new ScoreCalculator(scores, this.trumpTeam, lastWinner, this.beloteTeam())
                .getScoresForPoints();
    }

    private Optional<Team> beloteTeam() {
        return Optional.of(this.player(new Card(Figure.ROI, this.trumpColor)))
                .filter(player -> player.equals(this.player(new Card(Figure.DAME, this.trumpColor))))
                .map(Player::team);
    }

    private Player player(Card card) {
        return this.wonTricks.values().stream().flatMap(List::stream)
                .map(trick -> trick.player(card))
                .filter(Optional::isPresent).map(Optional::get)
                .findAny().orElseThrow();
    }
}
