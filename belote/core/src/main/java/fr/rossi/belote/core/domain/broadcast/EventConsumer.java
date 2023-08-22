package fr.rossi.belote.core.domain.broadcast;

import fr.rossi.belote.core.domain.event.Event;

public interface EventConsumer {

    default void consume(Event event) {
    }

}
