package fr.rossi.belote.core.card;

import java.util.List;

import static fr.rossi.belote.core.exception.TechnicalException.assertNotEquals;

public enum Figure {
    AS(11),
    DIX(10),
    ROI(4),
    DAME(3),
    VALET(2, 20),
    NEUF(0, 14),
    HUIT(0),
    SEPT(0);

    private final int points;
    private final int trumpPoints;
    private Integer order;

    Figure(int points, int trumpPoints) {
        this.points = points;
        this.trumpPoints = trumpPoints;
    }

    Figure(int points) {
        this(points, points);
    }

    private int getOrder() {
        if (this.order == null) {
            this.order = List.of(Figure.values()).indexOf(this);
        }
        return -this.order;
    }

    public int getPoints(boolean trump) {
        return trump ? this.trumpPoints : this.points;
    }

    public int compareTo(Figure figure, boolean trump) {
        int p1 = this.getPoints(trump);
        int p2 = figure.getPoints(trump);

        if (p1 == p2) {
            p1 = this.getOrder();
            p2 = figure.getOrder();
        }

        assertNotEquals("Error in figure order", p1, p2);
        return -Integer.compare(p1, p2);

    }
}