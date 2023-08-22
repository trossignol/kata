package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.rossi.belote.core.domain.Player;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;


@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        var module = new SimpleModule();
        module.addSerializer(Player.class, new PlayerSerializer());
        objectMapper.registerModule(module);
    }
}
