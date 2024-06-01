package fr.rossi.belote.server;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.CardAndPlayer;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.event.*;
import fr.rossi.belote.core.exception.ActionTimeoutException;
import fr.rossi.belote.core.exception.TechnicalException;
import fr.rossi.belote.core.player.SimplePlayer;
import fr.rossi.belote.server.async.AsyncWait;
import fr.rossi.belote.server.message.in.CardToPlay;
import fr.rossi.belote.server.message.in.InMessage;
import fr.rossi.belote.server.message.in.TrumpChoice;
import fr.rossi.belote.server.message.out.ChooseTrump;
import fr.rossi.belote.server.message.out.*;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fr.rossi.belote.server.async.AsyncWait.silentSleep;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ServerPlayer extends SimplePlayer {

    private final String id;
    private final GameSocket socket;

    public ServerPlayer(String name, GameSocket socket) {
        super(name);
        this.id = UUID.randomUUID().toString();
        this.socket = socket;
    }

    @Override
    public void consume(Event event) {
        switch (event) {
            case GameStarted e -> this.send(new EventMessage(e));
            case StartRound e -> this.send(new EventMessage(e));
            case TrumpChosen e -> this.sendAndWait(new EventMessage(e), 1);
            case TrickEnd e -> this.sendAndWait(new EventMessage(e), 2);
            case RoundEnd e -> this.sendAndWait(new EventMessage(e), 5);
            case GameEnd e -> this.send(new EventMessage(e));
            default -> log.debug("Unmanaged event: {}", event);
        }
    }

    @Override
    public Player addCards(List<Card> cards) {
        super.addCards(cards);
        this.sendAndWait(new AddCards(this.hand()), 1);
        return this;
    }

    @Override
    public Optional<Color> chooseTrump(Card card, boolean firstRun) throws ActionTimeoutException {
        final TrumpChoice params = this.sendAndWait(new ChooseTrump(firstRun ? 1 : 2, card));
        return Optional.ofNullable(params.color());
    }

    @Override
    protected Card cardToPlay(Trick trick, Collection<Card> playableCards) throws ActionTimeoutException {
        var cards = trick.cardsAndPlayers();
        var leader = cards.getWinner(trick.trumpColor()).map(CardAndPlayer::player).orElse(null);
        final CardToPlay params = this.sendAndWait(new PlayCard(cards, playableCards, leader));
        return params.card();
    }

    private void send(OutMessage message) {
        this.socket.send(this.name(), message.playerId(this.id).gameId(this.gameId()));
    }

    private void sendAndWait(OutMessage message, int timeInSeconds) {
        this.send(message);
        silentSleep(timeInSeconds * 1_000);
    }

    private <T extends InMessage> T sendAndWait(OutMessage message) throws ActionTimeoutException {
        final String uuid = AsyncWait.newUuid(this.name());
        this.send(message.uuid(uuid));
        log.info("Waiting for user={} - type={}", this.name(), message.getType());

        final Optional<T> response = AsyncWait.waitFor(uuid, 5,
                new ActionTimeoutException(message.getClass().getSimpleName(), this.name()));
        var responseMessage = response.orElseThrow();

        TechnicalException.assertEquals("Unexpected playerId", responseMessage.playerId(), this.id);
        TechnicalException.assertEquals("Unexpected gameId", responseMessage.gameId(), this.gameId());
        return responseMessage;
    }
}
