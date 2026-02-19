package io.hellorin.ciqual.database.model;

import java.util.Optional;

/**
 * Represents the nutritional composition of a food item in the Ciqual database.
 * Links an aliment (food) with a constituent (nutrient) and its value.
 */
public record Composition(
    String alimentCode,
    String constituentCode,
    Optional<Double> teneur,
    Optional<Double> min,
    Optional<Double> max,
    Optional<String> codeConfiance,
    Optional<String> sourceCode
) {

    /**
     * Creates a Composition with all required fields.
     */
    public Composition {
        if (alimentCode == null || alimentCode.isBlank()) {
            throw new IllegalArgumentException("Aliment code cannot be null or blank");
        }
        if (constituentCode == null || constituentCode.isBlank()) {
            throw new IllegalArgumentException("Constituent code cannot be null or blank");
        }
    }

    /**
     * Creates a Composition from XML field values.
     */
    public static Composition fromXml(String alimentCode, String constituentCode,
                                     String teneur, String min, String max,
                                     String codeConfiance, String sourceCode) {
        return new Composition(
            alimentCode.trim(),
            constituentCode.trim(),
            parseOptionalDouble(teneur),
            parseOptionalDouble(min),
            parseOptionalDouble(max),
            parseOptionalString(codeConfiance),
            parseOptionalString(sourceCode)
        );
    }

    private static Optional<Double> parseOptionalDouble(String value) {
        if (value == null || value.isBlank() || value.trim().equals("-") || value.trim().equals(" ")) {
            return Optional.empty();
        }
        try {
            // Replace comma with dot for French decimal notation
            String normalized = value.trim().replace(',', '.');
            return Optional.of(Double.parseDouble(normalized));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<String> parseOptionalString(String value) {
        if (value == null || value.isBlank() || value.trim().equals(" ") || value.trim().equals(".")) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }
}
