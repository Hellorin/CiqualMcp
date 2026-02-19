package io.hellorin.ciqual.database.model;

import java.util.Optional;

/**
 * Represents a nutrient or constituent in the Ciqual database.
 */
public record Constituent(
    String code,
    String nameFr,
    String nameEn,
    Optional<String> infoodsCode
) {

    /**
     * Creates a Constituent with all required fields.
     */
    public Constituent {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be null or blank");
        }
        if (nameFr == null || nameFr.isBlank()) {
            throw new IllegalArgumentException("French name cannot be null or blank");
        }
        if (nameEn == null || nameEn.isBlank()) {
            throw new IllegalArgumentException("English name cannot be null or blank");
        }
    }

    /**
     * Creates a Constituent from XML field values.
     */
    public static Constituent fromXml(String code, String nomFr, String nomEng, String codeInfoods) {
        return new Constituent(
            code.trim(),
            nomFr.trim(),
            nomEng.trim(),
            parseOptionalString(codeInfoods)
        );
    }

    private static Optional<String> parseOptionalString(String value) {
        if (value == null || value.isBlank() || value.trim().equals(" ")) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }
}
