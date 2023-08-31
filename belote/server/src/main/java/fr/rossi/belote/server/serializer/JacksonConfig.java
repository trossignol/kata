package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.server.message.out.EventMessage;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;


@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        objectMapper.registerModule(new SimpleModule()
                .addSerializer(Player.class, new PlayerSerializer())
                .addSerializer(EventMessage.class, new EventMessageSerializer()));
    }
}
