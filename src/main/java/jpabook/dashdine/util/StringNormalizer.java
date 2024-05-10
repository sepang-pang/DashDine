package jpabook.dashdine.util;

public class StringNormalizer {
    public static String normalizeString(String input) {
        return input.replaceAll("\\s+", "").toLowerCase();
    }
}
