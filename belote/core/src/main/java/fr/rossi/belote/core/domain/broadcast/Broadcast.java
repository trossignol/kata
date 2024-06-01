package fr.rossi.belote.core.domain.broadcast;

import fr.rossi.belote.core.domain.event.Event;
import fr.rossi.belote.core.utils.ThreadUtils;

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
        consumer.setBroadcast(this);
    }

    @Override
    public void consume(Event event) {
        ThreadUtils.parallel(
                consumers.stream()
                        .map(consumer -> (Runnable) () -> consumer.consume(event))
                        .toList()
                , true);
    }
}
