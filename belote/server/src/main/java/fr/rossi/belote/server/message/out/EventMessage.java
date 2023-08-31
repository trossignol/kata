package fr.rossi.belote.server.message.out;

import fr.rossi.belote.core.domain.event.Event;

public final class EventMessage extends OutMessage {

    private final Event event;

    public EventMessage(Event event) {
        super();
        this.event = event;
    }

    public Event event() {
        return this.event;
    }

    @Override
    public String getType() {
        return this.event.getClass().getSimpleName();
    }
}
