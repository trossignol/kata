package fr.rossi.belote.core.domain.broadcast;

import fr.rossi.belote.core.domain.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Broadcast implements EventConsumer {

    private final Collection<EventConsumer> consumers;

    public Broadcast(EventConsumer... consumers) {
        super();
        this.consumers = new ArrayList<>();
        this.consumers.addAll(List.of(consumers));
    }

    public void subscribe(EventConsumer consumer) {
        this.consumers.add(consumer);
    }

    @Override
    public void consume(Event event) {
        this.consumers.forEach(consumer -> consumer.consume(event));
    }
}
