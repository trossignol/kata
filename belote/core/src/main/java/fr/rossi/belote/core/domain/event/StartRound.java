package fr.rossi.belote.core.domain.event;

import fr.rossi.belote.core.domain.Player;

public record StartRound(Player firstPlayer) implements Event {
}
