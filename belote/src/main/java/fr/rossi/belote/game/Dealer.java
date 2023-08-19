package fr.rossi.belote.game;

import fr.rossi.belote.card.Card;
import fr.rossi.belote.domain.Player;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import static fr.rossi.belote.exception.TechnicalException.assertGreaterOrEquals;
import static fr.rossi.belote.exception.TechnicalException.assertTrue;

@Log
public class Dealer {
    private final List<Player> players;
    private final ArrayList<Card> cards;

    public Dealer(List<Player> players, List<Card> cards) {
        super();
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
        log.info("Choose trump: " + card);
        for (var round = 1; round <= 2; round++) {
            for (Player player : players) {
                var playerResponse = player.chooseTrump(card, round == 1);
                if (playerResponse.isPresent()) {
                    var color = playerResponse.get();
                    assertTrue(String.format("Error in trump selection (round=%d / card=%s / selected=%s)",
                                    round, card, color),
                            color == card.color() ^ round == 2);
                    log.info("[Round  " + round + "] " + player + " choose " + color);
                    return Optional.of(new TrumpParams(player, color));
                }
                log.info("[Round  " + round + "] " + player + " passed");
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
