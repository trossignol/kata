package fr.rossi.belote.core.utils;

import java.util.Collection;
import java.util.function.Predicate;

public final class Reducer {

    private Reducer() {
        super();
    }

    public static <T> boolean runAllAndReduce(Collection<T> list, Predicate<T> predicate) {
        // Do not use "anyMatch" to ensure that all the predicate are run
        return !list.stream().filter(predicate).toList().isEmpty();
    }
}
