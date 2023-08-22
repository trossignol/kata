package fr.rossi.belote.core.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record Card(Figure figure, Color color) implements Comparable<Card> {

    public static List<Card> getCards() {
        var cards = new ArrayList<>(Arrays.stream(Figure.values())
                .flatMap(figure -> Arrays.stream(Color.values())
                        .map(color -> new Card(figure, color)))
                .toList());
        Collections.shuffle(cards);
        return cards;
    }

    public static int nbCards() {
        return Figure.values().length * Color.values().length;
    }

    public int getPoints(Color trump) {
        return figure.getPoints(this.color == trump);
    }

    @Override
    public String toString() {
        return this.figure + "-" + this.color;
    }

    @Override
    public int compareTo(Card o) {
        return this.color == o.color
                ? this.figure.compareTo(o.figure)
                : this.color.compareTo(o.color);
    }
}