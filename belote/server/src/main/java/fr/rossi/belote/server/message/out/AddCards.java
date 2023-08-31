package fr.rossi.belote.server.message.out;

import fr.rossi.belote.core.card.Card;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class AddCards extends OutMessage {

    private final List<Card> cards;
}
