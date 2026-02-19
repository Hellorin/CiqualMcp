package io.hellorin.ciqual.database.model;

import java.util.Optional;

/**
 * Represents a data source reference in the Ciqual database.
 */
public record Source(
    String code,
    Optional<String> citation
) {

    /**
     * Creates a Source with all required fields.
     */
    public Source {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be null or blank");
        }
    }

    /**
     * Creates a Source from XML field values.
     */
    public static Source fromXml(String code, String citation) {
        return new Source(
            code.trim(),
            parseOptionalString(citation)
        );
    }

    private static Optional<String> parseOptionalString(String value) {
        if (value == null || value.isBlank() || value.trim().equals(" ")) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }
}
