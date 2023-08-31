package fr.rossi.belote.server.message.out;

import fr.rossi.belote.core.card.Card;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ChooseTrump extends OutMessage {

    private final int round;
    private final Card card;
}
