package fr.rossi.belote.domain;

import fr.rossi.belote.Params;

import java.util.List;

import static fr.rossi.belote.exception.TechnicalException.assertEquals;
import static fr.rossi.belote.exception.TechnicalException.assertLowerOrEquals;

public record Team(int id, List<Player> players) {

    public Team {
        assertLowerOrEquals("Error in team id", id, Params.NB_TEAMS);
        assertEquals("Error in number of players for team=" + this, Params.NB_PLAYERS_BY_TEAM, players.size());
        players.forEach(player -> player.team(this));
    }
}
