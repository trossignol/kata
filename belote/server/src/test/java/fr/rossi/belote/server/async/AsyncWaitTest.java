package fr.rossi.belote.server.async;

import fr.rossi.belote.core.exception.TechnicalException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import static fr.rossi.belote.server.async.AsyncWait.*;
import static org.junit.jupiter.api.Assertions.*;

class AsyncWaitTest {

    private static void doLater(int timeInMs, Runnable runnable) {
        new Thread(() -> {
            silentSleep(timeInMs);
            runnable.run();
        }).start();
    }

    @Test
    void testAsyncWait() {
        final String uuid = RandomStringUtils.random(10);
        final String response = "response";

        doLater(1_000, () -> response(uuid, response));
        assertEquals(response, waitFor(uuid, 10, true).orElseThrow());
    }

    @Test
    void testEndOfDelayAllowed() {
        final String uuid = RandomStringUtils.random(10);
        final String response = "response";

        doLater(10_000, () -> response(uuid, response));
        assertTrue(waitFor(uuid, 1, false).isEmpty());
    }

    @Test
    void testEndOfDelayNotAllowed() {
        final String uuid = RandomStringUtils.random(10);
        final String response = "response";

        doLater(10_000, () -> response(uuid, response));
        assertThrows(TechnicalException.class, () -> waitFor(uuid, 1, true));
    }

    @Test
    void testDoubleResponse() {
        final String uuid = RandomStringUtils.random(10);
        final String response = "response";

        doLater(1_000, () -> response(uuid, response));
        doLater(1_002, () -> assertThrows(TechnicalException.class, () -> response(uuid, response)));
        assertEquals(response, waitFor(uuid, 10, true).orElseThrow());
    }

    @Test
    void testUnexpectedResponse() {
        final String uuid = RandomStringUtils.random(10);
        final String response = "response";
        assertThrows(TechnicalException.class, () -> response(uuid, response));
    }
}
