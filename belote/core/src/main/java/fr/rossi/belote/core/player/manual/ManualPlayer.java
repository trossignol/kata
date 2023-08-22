package fr.rossi.belote.core.player.manual;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.event.Event;
import fr.rossi.belote.core.domain.event.TrickEnd;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import fr.rossi.belote.core.player.SimplePlayer;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ManualPlayer extends SimplePlayer {

    private final CardsPrinter printer;

    public ManualPlayer(String name) {
        super(name);
        this.printer = new CardsPrinter();
    }

    @Override
    public Optional<Color> chooseTrump(Card card, boolean firstRun) {
        if (firstRun) {
            this.printer.printCards(this.hand());
            return this.printer.choose(String.format("Choose %s as trump", card), List.of(true, false))
                    ? Optional.of(card.color())
                    : Optional.empty();
        }

        return Optional.ofNullable(
                this.printer.choose("Choose trump color", List.of(Color.values()), true));
    }

    @Override
    public void consume(Event event) {
        switch (event) {
            case TrumpChosen e -> log.debug("Trump chosen: {}", e);
            case TrickEnd e -> log.debug("Trick ended: {}", e);
            default -> log.debug("Unmanaged event: {}", event);
        }
    }

    @Override
    protected Card cardToPlay(Trick trick, Collection<Card> playableCards) {
        this.printer.printTable(trick.cardsAndPlayers());
        return this.printer.choose("Choose a card", playableCards);
    }
}
