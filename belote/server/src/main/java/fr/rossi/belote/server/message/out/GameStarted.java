package fr.rossi.belote.server.message.out;

import fr.rossi.belote.core.domain.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class GameStarted extends OutMessage {

    private final List<Player> players;
}
