package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.rossi.belote.core.domain.Player;

import java.io.IOException;

class PlayerSerializer extends StdSerializer<Player> {

    PlayerSerializer() {
        super(Player.class);
    }

    @Override
    public void serialize(Player player, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", player.name());
        if (player.team() != null)
            gen.writeStringField("team", player.team().toString());
        gen.writeEndObject();
    }
}