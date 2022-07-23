package fr.rossi.fizzbuzz;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class FizzBuzz {
    private static List<Rule> SORTED_RULES = List.of(
            new Rule(3, "Fizz"),
            new Rule(5, "Buzz"));

    static String fizzbuzz(int value) {
        final String result = SORTED_RULES.stream()
                .filter(rule -> rule.isMultiple(value))
                .map(rule -> rule.label)
                .collect(Collectors.joining());
        return StringUtils.defaultIfEmpty(result, Integer.toString(value));
    }

    private static record Rule(int divider, String label) {
        public boolean isMultiple(int number) {
            return number % this.divider == 0;
        }
    }
}
