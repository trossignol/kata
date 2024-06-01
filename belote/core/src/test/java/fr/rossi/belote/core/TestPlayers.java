package fr.rossi.belote.core;

import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.game.PlayersHandler;
import fr.rossi.belote.core.player.SimplePlayer;

import java.util.List;

public class TestPlayers {

    public static final SimplePlayer P1 = new SimplePlayer("1");
    public static final SimplePlayer P2 = new SimplePlayer("2");
    public static final SimplePlayer P3 = new SimplePlayer("3");
    public static final SimplePlayer P4 = new SimplePlayer("4");

    public static final Team T1 = new Team(1, List.of(P1, P3));
    public static final Team T2 = new Team(2, List.of(P2, P4));
    public static final List<Team> TEAMS = List.of(T1, T2);

    public static final PlayersHandler PLAYERS = new PlayersHandler(TEAMS, List.of(P1, P2, P3, P4));
}
