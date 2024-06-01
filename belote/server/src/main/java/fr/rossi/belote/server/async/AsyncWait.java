package fr.rossi.belote.server.async;

import fr.rossi.belote.core.exception.ActionTimeoutException;
import fr.rossi.belote.core.exception.TechnicalException;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncWait {

    private static final Set<String> waitedKeys = ConcurrentHashMap.newKeySet();
    private static final Map<String, Object> store = new ConcurrentHashMap<>();

    private AsyncWait() {
    }

    public static String newUuid(String username) {
        String uuid;
        do {
            uuid = String.format("%s-%s", username, UUID.randomUUID());
        } while (waitedKeys.contains(uuid));
        return uuid;
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> waitFor(String uuid, int delayInSeconds, ActionTimeoutException exception) throws ActionTimeoutException {
        waitedKeys.add(uuid);
        var stepInMs = 100;
        var delayInMs = delayInSeconds * 1_000;

        for (var i = 0; i < delayInMs / stepInMs; i++) {
            silentSleep(stepInMs);
            var response = Optional.ofNullable(store.get(uuid));
            if (response.isPresent()) {
                store.remove(uuid);
                return (Optional<T>) response;
            }
        }

        throw exception;
    }

    public static void response(String uuid, Object response) {
        TechnicalException.assertNotNull("Uuid is required", uuid);
        TechnicalException.assertNotNull("Response is required", response);
        TechnicalException.assertTrue(
                String.format("Response not expected for uuid=%s (value=%s)", uuid, response),
                waitedKeys.contains(uuid));
        TechnicalException.assertFalse(
                String.format("Response already in store for uuid=%s (value=%s)", uuid, response),
                store.containsKey(uuid));

        store.put(uuid, response);
        waitedKeys.remove(uuid);
    }

    @SneakyThrows
    public static void silentSleep(int timeInMs) {
        Thread.sleep(timeInMs);
    }
}
