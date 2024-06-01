package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.player.SimplePlayer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.rossi.belote.server.serializer.SerializerTestHelper.assertEqualsMap;
import static fr.rossi.belote.server.serializer.SerializerTestHelper.serialize;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamSerializerTest {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        new JacksonConfig().customize(jsonMapper);
    }

    @Test
    void testSerialize() {
        var player1 = new SimplePlayer("player-1");
        var player2 = new SimplePlayer("player-2");
        var team = new Team(1, List.of(player1, player2));
        var json = serialize(team);

        assertEquals(team.id(), json.get("id"));
        assertEquals(team.toString(), json.get("name"));
        assertEqualsMap(List.of(player1, player2), json.get("players"));
    }
}
