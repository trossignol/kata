package fr.rossi.belote.server.message.out;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.CardAndPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PlayCard extends OutMessage {

    private final List<CardAndPlayer> table;
    private final Collection<Card> playableCards;
}
