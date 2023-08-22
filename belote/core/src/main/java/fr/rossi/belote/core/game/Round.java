package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.broadcast.Broadcast;
import fr.rossi.belote.core.domain.event.RoundEnd;
import fr.rossi.belote.core.domain.event.TrickEnd;
import fr.rossi.belote.core.score.ScoreCalculator;
import fr.rossi.belote.core.utils.Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.rossi.belote.core.exception.TechnicalException.assertNotNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class Round {

    private static final int NB_TRICKS = Card.nbCards() / Params.NB_PLAYERS;

    private final Broadcast broadcast;
    private final PlayersHandler players;
    private final Color trumpColor;
    private final Team trumpTeam;
    private final Map<Team, List<TrickImpl>> wonTricks;

    Round(Broadcast broadcast, PlayersHandler players, Color trumpColor, Team trumpTeam) {
        super();
        this.broadcast = broadcast;
        this.players = players;
        this.trumpColor = trumpColor;
        this.trumpTeam = trumpTeam;
        this.wonTricks = this.players.teams().stream()
                .collect(toMap(identity(), t -> new ArrayList<>(NB_TRICKS)));
    }

    public RoundEnd run() {
        Player winnerPlayer = null;
        var currentPlayers = this.players;

        for (int i = 0; i < NB_TRICKS; i++) {
            var trick = new TrickImpl(this.trumpColor);
            winnerPlayer = trick.run(currentPlayers.players());
            this.broadcast.consume(new TrickEnd(trick.cardsAndPlayers(), winnerPlayer));
            this.wonTricks.get(winnerPlayer.team()).add(trick);
            currentPlayers = currentPlayers.next(winnerPlayer);
        }
        assertNotNull("No winner player found", winnerPlayer);
        return scoresForTricks(winnerPlayer.team());
    }

    private RoundEnd scoresForTricks(Team lastWinner) {
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
