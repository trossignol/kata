package fr.rossi.belote.core.exception;

import java.util.Objects;

public class TechnicalException extends RuntimeException {

    public TechnicalException(String msg) {
        super(msg);
    }

    public TechnicalException(String msg, Throwable err) {
        super(msg, err);
    }

    public static void assertTrue(String msg, boolean condition) {
        if (!condition) {
            throw new TechnicalException(msg);
        }
    }

    public static void assertFalse(String msg, boolean condition) {
        assertTrue(msg, !condition);
    }

    public static void assertEquals(String msg, Object value, Object expected) {
        if (!Objects.equals(value, expected)) {
            throw new TechnicalException(String.format("%s (value=%s should equals expected=%s)", msg, value, expected));
        }
    }

    public static void assertNotEquals(String msg, Object value, Object expected) {
        if (Objects.equals(value, expected)) {
            throw new TechnicalException(String.format("%s (value=%s should not equals expected=%s", msg, value, expected));
        }
    }

    public static void assertLower(String msg, int value, int expected) {
        if (value >= expected) {
            throw new TechnicalException(String.format("%s (value=%s should be lower than expected=%s)", msg, value, expected));
        }
    }

    public static void assertLowerOrEquals(String msg, int value, int expected) {
        if (value > expected) {
            throw new TechnicalException(String.format("%s (value=%s should be lower or equals than expected=%s)", msg, value, expected));
        }
    }

    public static void assertGreater(String msg, int value, int expected) {
        if (value <= expected) {
            throw new TechnicalException(String.format("%s (value=%s should be greater than expected=%s)", msg, value, expected));
        }
    }

    public static void assertGreaterOrEquals(String msg, int value, int expected) {
        if (value < expected) {
            throw new TechnicalException(String.format("%s (value=%s should be greater or equals than expected=%s)", msg, value, expected));
        }
    }

    public static void assertNotNull(String msg, Object value) {
        if (value == null) {
            throw new TechnicalException(msg);
        }
    }

    public static void assertNull(String msg, Object value) {
        if (value != null) {
            throw new TechnicalException(String.format("%s (value=%s should not be null)", msg, value));
        }
    }

    public static void assertNull(String msg, Throwable error) {
        if (error != null) {
            throw new TechnicalException(msg, error);
        }
    }
}
