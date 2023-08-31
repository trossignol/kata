package fr.rossi.belote.core.player.manual;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.CardsAndPlayers;
import fr.rossi.belote.core.exception.TechnicalException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class CardsPrinter {

    void printTable(CardsAndPlayers cardsAndPlayers) {
        println(String.format("Table: %s", cardsAndPlayers));
    }

    void printCards(List<Card> inCards) {
        var cards = new ArrayList<>(inCards);
        Collections.sort(cards);
        println(StringUtils.join(cards, " / "));
    }

    void printOptionsToChoose(String msg, List<?> options, boolean nullAllowed) {
        println(String.format("%s: %s%s", msg,
                nullAllowed ? "0 - No / " : StringUtils.EMPTY,
                IntStream.rangeClosed(1, options.size())
                        .mapToObj(i -> String.format("%d - %s", i, options.get(i - 1)))
                        .collect(joining(" / "))));
    }

    <T extends Comparable<T>> T choose(String msg, Collection<T> inOptions, boolean nullAllowed) {
        var options = new ArrayList<>(inOptions);
        Collections.sort(options);
        printOptionsToChoose(msg, options, nullAllowed);

        var sc = new Scanner(System.in);
        for (int i = 0; i < 10; i++) {
            var input = sc.nextLine();
            if (!NumberUtils.isDigits(input)) continue;

            var id = Integer.parseInt(input);
            if (id == 0 && nullAllowed) return null;
            if (id > 0 && id <= options.size()) return options.get(id - 1);
        }
        throw new TechnicalException("Too many attempts");
    }

    private void println(String msg) {
        System.out.println(msg);
    }
}