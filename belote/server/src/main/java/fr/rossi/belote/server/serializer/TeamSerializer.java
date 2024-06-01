package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.rossi.belote.core.domain.Team;
import lombok.SneakyThrows;

import java.io.IOException;

class TeamSerializer extends StdSerializer<Team> {

    TeamSerializer() {
        super(Team.class);
    }

    @Override
    @SneakyThrows(ReflectiveOperationException.class)
    public void serialize(Team team, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        for (var component : Team.class.getRecordComponents()) {
            gen.writeObjectField(component.getName(), component.getAccessor().invoke(team));
        }
        gen.writeStringField("name", team.toString());
        gen.writeEndObject();
    }
}