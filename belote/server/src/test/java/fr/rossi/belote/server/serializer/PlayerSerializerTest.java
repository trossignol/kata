package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.player.SimplePlayer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.rossi.belote.server.serializer.SerializerTestHelper.serialize;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerSerializerTest {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        new JacksonConfig().customize(jsonMapper);
    }

    @Test
    void testSerialize() {
        var player = new SimplePlayer("player-name");
        var team = new Team(1, List.of(player, new SimplePlayer("player-2")));
        var json = serialize(player);

        assertEquals(player.name(), json.get("name"));
        assertEquals(team.toString(), json.get("team"));
    }
}
