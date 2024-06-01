package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializerTestHelper {


    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        new JacksonConfig().customize(jsonMapper);
    }

    static void assertEqualsMap(Object expected, Object value) {
        if (Collection.class.isAssignableFrom(expected.getClass())) {
            assertEquals(jsonMapper.convertValue(expected, List.class), value);
        } else {
            assertEquals(jsonMapper.convertValue(expected, Map.class), value);
        }
    }

    @SneakyThrows
    static Map<String, Object> serialize(Object object) {
        var json = jsonMapper.convertValue(object, Map.class);
        System.out.println(jsonMapper.writeValueAsString(object));
        System.out.println(json);
        return json;
    }
}
