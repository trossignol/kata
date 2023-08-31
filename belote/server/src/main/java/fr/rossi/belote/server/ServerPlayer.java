package fr.rossi.belote.server;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.event.Event;
import fr.rossi.belote.core.domain.event.RoundEnd;
import fr.rossi.belote.core.domain.event.TrickEnd;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import fr.rossi.belote.core.exception.TechnicalException;
import fr.rossi.belote.core.player.SimplePlayer;
import fr.rossi.belote.server.async.AsyncWait;
import fr.rossi.belote.server.message.in.CardToPlay;
import fr.rossi.belote.server.message.in.InMessage;
import fr.rossi.belote.server.message.in.TrumpChoice;
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

    public void start(List<Player> players) {
        this.send(new GameStarted(players));
    }

    @Override
    public void consume(Event event) {
        switch (event) {
            case TrumpChosen e -> this.sendAndWait(new EventMessage(e), 1);
            case TrickEnd e -> this.sendAndWait(new EventMessage(e), 5);
            case RoundEnd e -> this.sendAndWait(new EventMessage(e), 5);
            default -> log.debug("Unmanaged event: {}", event);
        }
    }

    @Override
    public Player addCards(List<Card> cards) {
        this.send(new AddCards(cards));
        return super.addCards(cards);
    }

    @Override
    public Optional<Color> chooseTrump(Card card, boolean firstRun) {
        final TrumpChoice params = this.sendAndWait(new ChooseTrump(firstRun ? 1 : 2, card));
        return Optional.ofNullable(params.color());
    }

    @Override
    protected Card cardToPlay(Trick trick, Collection<Card> playableCards) {
        final CardToPlay params = this.sendAndWait(
                new PlayCard(trick.cardsAndPlayers(), playableCards));
        return params.card();
    }

    private void send(OutMessage message) {
        this.socket.send(this.name(), message.playerId(this.id).gameId(this.gameId()));
    }

    private void sendAndWait(OutMessage message, int timeInSeconds) {
        this.send(message);
        silentSleep(timeInSeconds * 1_000);
    }

    private <T extends InMessage> T sendAndWait(OutMessage message) {
        final String uuid = AsyncWait.newUuid(this.name());
        this.send(message.uuid(uuid));
        log.info("Waiting for user={} - type={}", this.name(), message.getType());

        final Optional<T> response = AsyncWait.waitFor(uuid, 30, true);
        var responseMessage = response.orElseThrow();

        TechnicalException.assertEquals("Unexpected playerId", responseMessage.playerId(), this.id);
        TechnicalException.assertEquals("Unexpected gameId", responseMessage.gameId(), this.gameId());
        return responseMessage;
    }
}
