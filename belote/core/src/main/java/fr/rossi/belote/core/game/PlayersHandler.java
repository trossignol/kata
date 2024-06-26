package fr.rossi.belote.core.game;

import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.utils.Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class PlayersHandler {

    private final List<Team> teams;
    private final List<Player> players;

    public PlayersHandler(List<Team> teams, List<Player> players) {
        super();
        this.teams = teams;
        this.players = players;
    }

    public PlayersHandler(List<Team> teams) {
        this(teams, IntStream.range(0, Params.NB_PLAYERS_BY_TEAM).boxed()
                .flatMap(playerId -> teams.stream().map(team -> team.players().get(playerId)))
                .toList());
    }

    public List<Team> teams() {
        return this.teams;
    }

    public List<Player> players() {
        return this.players;
    }

    public Optional<Player> playerByName(String name) {
        return this.players.stream().filter(p -> p.name().equals(name)).findAny();
    }

    public Team otherTeam(Team team) {
        return this.teams().stream().filter(t -> !t.equals(team)).findAny().orElseThrow();
    }

    private PlayersHandler next(int startIndex) {
        final List<Player> list = new ArrayList<>();
        for (int i = 0; i < this.players.size(); i++) {
            list.add(this.players.get((i + startIndex) % this.players.size()));
        }
        return new PlayersHandler(this.teams, list);
    }

    public PlayersHandler next() {
        return this.next(1);
    }

    public PlayersHandler next(Player player) {
        return this.next(this.players.indexOf(player));
    }
}
