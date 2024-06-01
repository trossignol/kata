package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.broadcast.Broadcast;
import fr.rossi.belote.core.domain.event.RoundEnd;
import fr.rossi.belote.core.domain.event.RoundStarted;
import fr.rossi.belote.core.domain.event.TrickEnd;
import fr.rossi.belote.core.exception.ActionTimeoutException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Round {

    private final Broadcast broadcast;
    private final PlayersHandler players;
    private final Color trumpColor;
    private final Team trumpTeam;
    private WonTricks wonTricks;

    Round(Broadcast broadcast, PlayersHandler players, Color trumpColor, Team trumpTeam) {
        super();
        this.broadcast = broadcast;
        this.players = players;
        this.trumpColor = trumpColor;
        this.trumpTeam = trumpTeam;
        this.wonTricks = new WonTricks(this.players.teams());
    }

    private static Trick runTrick(List<Player> players, Color trumpColor) throws ActionTimeoutException {
        var trick = new TrickImpl(trumpColor);

        for (Player player : players) {
            var card = player.play(trick);
            log.debug("{} play {}", player, card);
            trick = trick.addCard(player, card);
        }
        return trick;
    }

    public RoundEnd run() throws ActionTimeoutException {
        this.broadcast.consume(new RoundStarted());
        var currentPlayers = this.players;
        do {
            var trick = runTrick(currentPlayers.players(), this.trumpColor);
            this.wonTricks = this.wonTricks.add(trick);
            currentPlayers = currentPlayers.next(trick.winner().player());
            this.broadcast.consume(new TrickEnd(trick.cardsAndPlayers(), trick.winner().player(), this.wonTricks.currentScores()));
        } while (!this.wonTricks.completed());

        return this.wonTricks.scoresForTricks(this.trumpColor, this.trumpTeam, this.wonTricks.lastWinner().team());
    }
}
