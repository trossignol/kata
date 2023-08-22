package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rossi.belote.server.message.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

import java.util.Map;

@ApplicationScoped
public class MessageSerializer {

    @Inject
    ObjectMapper jsonMapper;

    @SneakyThrows
    public String write(Object message) {
        return this.jsonMapper.writeValueAsString(message);
    }

    @SneakyThrows
    public Message read(String json) {
        var map = this.jsonMapper.readValue(json, Map.class);
        var targetClass = Message.Type.valueOf((String) map.get("type")).targetClass();
        return this.jsonMapper.readValue(json, targetClass);
    }
}
