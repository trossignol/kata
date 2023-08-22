package fr.rossi.belote.server;

import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.game.Game;
import fr.rossi.belote.core.player.SimplePlayer;
import fr.rossi.belote.server.message.PlayCard;
import fr.rossi.belote.server.message.StartGame;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GameService {

    public Team startGame(String username, StartGame params) {
        var p1A = new SimplePlayer("1A");
        var p1B = new SimplePlayer("1B");
        var p2A = new SimplePlayer("2A");
        var p2B = new SimplePlayer("2B");

        var t1 = new Team(1, List.of(p1A, p1B));
        var t2 = new Team(2, List.of(p2A, p2B));

        return new Game(List.of(t1, t2)).play();

    }

    public void playCard(String username, PlayCard params) {

    }
}
