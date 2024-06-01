package fr.rossi.belote.core.utils;

import java.util.ArrayList;
import java.util.List;

public final class Combination {

    private Combination() {
        super();
    }

    public static <T> List<List<T>> generate(List<T> elements, int n) {
        final List<List<T>> combinations = new ArrayList<>();
        generateCombinationsRecursive(0, n, combinations, elements, new ArrayList<>());
        return combinations;
    }

    private static <T> void generateCombinationsRecursive(int start, int n, List<List<T>> combinations, List<T> elements, List<T> current) {
        if (n == 0) {
            combinations.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < elements.size(); i++) {
            current.add(elements.get(i));
            generateCombinationsRecursive(i + 1, n - 1, combinations, elements, current);
            current.remove(current.size() - 1);
        }
    }
}
