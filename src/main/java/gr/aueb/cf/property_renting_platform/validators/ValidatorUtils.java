package gr.aueb.cf.property_renting_platform.validators;

import java.util.List;
import java.util.Objects;

public class ValidatorUtils {
    public static List<String> getNormalisedStrings(List<String> strings) {
        return strings.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();
    }

}
