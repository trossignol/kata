package fr.rossi.belote.core.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombinationTest {

    @Test
    void testCombination() {
        assertEquals(List.of(List.of(1, 2), List.of(1, 3), List.of(2, 3)),
                Combination.generate(List.of(1, 2, 3), 2));
    }
}
