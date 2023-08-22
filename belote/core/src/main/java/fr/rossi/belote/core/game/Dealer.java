package fr.rossi.belote.core.game;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.broadcast.Broadcast;
import fr.rossi.belote.core.domain.event.ChooseTrump;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import static fr.rossi.belote.core.exception.TechnicalException.assertGreaterOrEquals;
import static fr.rossi.belote.core.exception.TechnicalException.assertTrue;

@Slf4j
public class Dealer {
    private final Broadcast broadcast;
    private final List<Player> players;
    private final ArrayList<Card> cards;

    public Dealer(Broadcast broadcast, List<Player> players, List<Card> cards) {
        super();
        this.broadcast = broadcast;
        this.players = players;
        this.cards = new ArrayList<>(cards);
    }

    public Optional<TrumpParams> deal() {
        this.players.forEach(Player::clearHand);
        // TODO TRO Player -2 Cut cards
        // TODO TRO Player -1 Deal (ask 3-2 or 2-3)

        this.deal(3);
        this.deal(2);
        // TODO TRO Ask for trump

        var trumpCard = this.popCards(1).get(0);
        return this.chooseTrump(trumpCard)
                .map(trumpParams -> {
                    trumpParams.player().addCards(List.of(trumpCard));
                    this.deal(player -> player.equals(trumpParams.player()) ? 2 : 3);
                    return trumpParams;
                });
    }

    private Optional<TrumpParams> chooseTrump(Card card) {
        log.debug("Choose trump: {}", card);
        this.broadcast.consume(new ChooseTrump(card, this.players.get(0)));
        for (var round = 1; round <= 2; round++) {
            for (Player player : this.players) {
                var playerResponse = player.chooseTrump(card, round == 1);
                if (playerResponse.isPresent()) {
                    var color = playerResponse.get();
                    assertTrue(String.format("Error in trump selection (round=%d / card=%s / selected=%s)",
                                    round, card, color),
                            color == card.color() ^ round == 2);
                    this.broadcast.consume(new TrumpChosen(card, player, color));
                    return Optional.of(new TrumpParams(player, color));
                }
                log.debug("[Round {}] {} passed", round, player);
            }
        }
        return Optional.empty();
    }

    private List<Card> popCards(int nb) {
        assertGreaterOrEquals("No more cards to pop", this.cards.size(), nb);
        return IntStream.range(0, nb).mapToObj(i -> this.cards.remove(0)).toList();
    }

    private void deal(int fixedNb) {
        this.deal(player -> fixedNb);
    }

    private void deal(ToIntFunction<Player> nbForPlayer) {
        this.players.forEach(player -> player.addCards(this.popCards(nbForPlayer.applyAsInt(player))));
    }

    public List<Card> collectCards() {
        // TODO Collect cards
        return Card.getCards();
    }
}
