package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.rossi.belote.core.domain.event.Event;
import fr.rossi.belote.core.exception.TechnicalException;
import fr.rossi.belote.server.message.out.EventMessage;
import fr.rossi.belote.server.message.out.OutMessage;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

class EventMessageSerializer extends StdSerializer<EventMessage> {

    private static final Map<Class<? extends Event>, Map<String, Method>> EVENT_FIELDS = new HashMap<>();

    EventMessageSerializer() {
        super(EventMessage.class);
    }


    @SneakyThrows
    private static void writeValue(JsonGenerator gen, String field, Method getter, Object obj) {
        gen.writeObjectField(field, getter.invoke(obj));
    }

    private static Map<String, Method> getFields(Class<? extends Event> eventClass) {
        return EVENT_FIELDS.computeIfAbsent(eventClass, eClass -> {
            if (eClass.isRecord()) {
                return Arrays.stream(eClass.getRecordComponents())
                        .collect(toMap(RecordComponent::getName, RecordComponent::getAccessor));
            }

            final Map<String, Method> fields = new HashMap<>();
            Arrays.stream(PropertyUtils.getPropertyDescriptors(eClass))
                    .forEach(field -> fields.put(field.getName(), field.getReadMethod()));

            var methods = Arrays.stream(eClass.getDeclaredMethods())
                    .filter(method -> ArrayUtils.isEmpty(method.getParameters()))
                    .collect(toMap(Method::getName, identity()));
            Arrays.stream(eClass.getDeclaredFields())
                    .map(Field::getName)
                    .filter(methods::containsKey)
                    .map(methods::get)
                    .forEach(method -> fields.put(method.getName(), method));

            return fields;
        });
    }


    @Override
    public void serialize(EventMessage message, JsonGenerator gen, SerializerProvider provider) throws IOException {
        TechnicalException.assertNotNull("Event in message should not be null", message.event());
        gen.writeStartObject();
        Arrays.stream(PropertyUtils.getPropertyDescriptors(OutMessage.class))
                .forEach(field -> writeValue(gen, field.getName(), field.getReadMethod(), message));

        getFields(message.event().getClass())
                .forEach((field, getter) -> writeValue(gen, field, getter, message.event()));

        gen.writeEndObject();
    }
}