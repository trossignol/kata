package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rossi.belote.core.exception.TechnicalException;
import fr.rossi.belote.server.message.in.InMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class MessageSerializer {

    @Inject
    ObjectMapper jsonMapper;

    @SneakyThrows
    public String write(Object message) {
        return this.jsonMapper.writeValueAsString(message);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public InMessage read(String json) {
        var map = this.jsonMapper.readValue(json, Map.class);

        final Class<? extends InMessage> targetClass = (Class<? extends InMessage>) Arrays
                .stream(InMessage.class.getPermittedSubclasses())
                .filter(c -> c.getSimpleName().equals(map.get("type")))
                .findAny()
                .orElseThrow(() -> new TechnicalException("Unvalid message type=" + map.get("type")));
        return this.jsonMapper.readValue(json, targetClass);
    }
}
