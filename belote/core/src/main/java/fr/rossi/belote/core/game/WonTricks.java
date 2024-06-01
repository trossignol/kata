package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.event.RoundEnd;
import fr.rossi.belote.core.score.ScoreCalculator;
import fr.rossi.belote.core.utils.Params;

import java.util.*;

import static fr.rossi.belote.core.exception.TechnicalException.assertFalse;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class WonTricks {

    private static final int NB_TRICKS = Card.nbCards() / Params.NB_PLAYERS;

    private final Collection<Team> teams;
    private final List<Trick> tricks;

    public WonTricks(Collection<Team> teams) {
        super();
        this.teams = teams;
        this.tricks = new ArrayList<>(NB_TRICKS);
    }

    private WonTricks(WonTricks source, Trick trick) {
        super();
        this.teams = source.teams;
        this.tricks = new ArrayList<>(source.tricks);
        this.tricks.add(trick);
    }

    public boolean completed() {
        return this.tricks.size() >= NB_TRICKS;
    }

    public Player lastWinner() {
        assertFalse("Can't get last winner (no trick)", this.tricks.isEmpty());
        return this.tricks.get(this.tricks.size() - 1).winner().player();
    }

    public WonTricks add(Trick trick) {
        return new WonTricks(this, trick);
    }

    private Optional<Team> beloteTeam(Color trumpColor) {
        return Optional.of(player(new Card(Figure.ROI, trumpColor)))
                .filter(player -> player.equals(player(new Card(Figure.DAME, trumpColor))))
                .map(Player::team);
    }

    private Player player(Card card) {
        return this.tricks.stream()
                .map(trick -> trick.player(card))
                .filter(Optional::isPresent).map(Optional::get)
                .findAny().orElseThrow();
    }

    public Map<Team, Integer> currentScores() {
        return this.tricks.stream().collect(groupingBy(trick -> trick.winner().player().team(), summingInt(Trick::getPoints)));
    }

    public RoundEnd scoresForTricks(Color trumpColor, Team trumpTeam, Team lastWinner) {
        return new ScoreCalculator(this.teams, this.currentScores(), trumpTeam, lastWinner, this.beloteTeam(trumpColor))
                .getScoresForPoints();
    }
}
