package fr.rossi.belote.core.exception;

import org.junit.jupiter.api.Test;

import static fr.rossi.belote.core.exception.TechnicalException.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TechnicalExceptionTest {

    @Test
    void testAssertTrue() {
        assertTrue("", true);
        assertThrows(TechnicalException.class,
                () -> assertTrue("", false));
    }

    @Test
    void testAssertFalse() {
        assertFalse("", false);
        assertThrows(TechnicalException.class,
                () -> assertFalse("", true));
    }

    @Test
    void testAssertEquals() {
        assertEquals("", 1, 1);
        assertThrows(TechnicalException.class,
                () -> assertEquals("", 1, 2));
    }

    @Test
    void testAssertNotEquals() {
        assertNotEquals("", 1, 2);
        assertThrows(TechnicalException.class,
                () -> assertNotEquals("", 1, 1));
    }

    @Test
    void testAssertNull() {
        assertNull("", null);
        assertThrows(TechnicalException.class,
                () -> assertNull("", 1));
    }

    @Test
    void testAssertNotNull() {
        assertNotNull("", 1);
        assertThrows(TechnicalException.class,
                () -> assertNotNull("", null));
    }

    @Test
    void testAssertLower() {
        assertLower("", 1, 2);
        assertThrows(TechnicalException.class,
                () -> assertLower("", 1, 1));
        assertThrows(TechnicalException.class,
                () -> assertLower("", 2, 1));
    }

    @Test
    void testAssertLowerOrEquals() {
        assertLowerOrEquals("", 1, 2);
        assertLowerOrEquals("", 1, 1);
        assertThrows(TechnicalException.class,
                () -> assertLowerOrEquals("", 2, 1));
    }

    @Test
    void testAssertGreater() {
        assertGreater("", 2, 1);
        assertThrows(TechnicalException.class,
                () -> assertGreater("", 1, 1));
        assertThrows(TechnicalException.class,
                () -> assertGreater("", 1, 2));
    }

    @Test
    void testAssertGreaterOrEquals() {
        assertGreaterOrEquals("", 2, 1);
        assertGreaterOrEquals("", 1, 1);
        assertThrows(TechnicalException.class,
                () -> assertGreaterOrEquals("", 1, 2));
    }
}
