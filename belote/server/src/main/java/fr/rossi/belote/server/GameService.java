package fr.rossi.belote.server;

import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.game.Game;
import fr.rossi.belote.core.player.brain.BrainPlayer;
import fr.rossi.belote.server.async.AsyncWait;
import fr.rossi.belote.server.message.in.InMessage;
import fr.rossi.belote.server.message.in.StartGame;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@ApplicationScoped
public class GameService {

    private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    @Inject
    GameSocket socket;

    public void handleMessage(String username, InMessage message) {
        if (Objects.requireNonNull(message) instanceof StartGame m) {
            this.startGame(username, m);
        } else {
            AsyncWait.response(message.uuid(), message);
        }
    }

    void startGame(String username, StartGame params) {
        executor.execute(() -> {
            log.info("Start game for user={}", username);
            var p1A = new ServerPlayer(username, this.socket);
            var p1B = new BrainPlayer("Partner");
            var p2A = new BrainPlayer("Opponent 1");
            var p2B = new BrainPlayer("Opponent 2");

            var t1 = new Team(1, List.of(p1A, p1B));
            var t2 = new Team(2, List.of(p2A, p2B));

            new Game(List.of(t1, t2)).play();
        });
    }
}
