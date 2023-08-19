package fr.rossi.belote.domain.broadcast;

import fr.rossi.belote.domain.event.Event;

public interface EventConsumer {

    default void consume(Event event) {
    }

}
