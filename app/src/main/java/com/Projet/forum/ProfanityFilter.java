package com.Projet.forum;

import java.util.Arrays;
import java.util.List;

public class ProfanityFilter {

    // Your requested list + common additions
    private static final List<String> BLACKLIST = Arrays.asList(
            "fuck", "shit", "bitch", "slut", "motherfucker",
            "cunt", "asshole", "piss", "dick", "pussy"
    );

    /**
     * Replaces blacklisted words with asterisks.
     * Example: "That is shit" -> "That is ****"
     */
    public static String filter(String input) {
        if (input == null || input.isEmpty()) return input;

        String filteredText = input;

        for (String word : BLACKLIST) {
            // (?i) = case insensitive
            // \\b = word boundary (prevents filtering "button" if "butt" is in list)
            String regex = "(?i)\\b" + word + "\\b";

            // Create asterisk string of same length
            StringBuilder asterisks = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                asterisks.append("*");
            }

            filteredText = filteredText.replaceAll(regex, asterisks.toString());
        }

        return filteredText;
    }
}
