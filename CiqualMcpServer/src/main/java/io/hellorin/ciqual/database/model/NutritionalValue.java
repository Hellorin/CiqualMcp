package io.hellorin.ciqual.database.model;

import java.util.Optional;

/**
 * Represents a nutritional value for a specific nutrient in a food item.
 * Combines composition data with constituent information.
 */
public record NutritionalValue(
    Constituent constituent,
    Optional<Double> value,
    Optional<Double> min,
    Optional<Double> max,
    Optional<String> confidenceCode,
    Optional<String> sourceCode
) {

    /**
     * Gets the nutrient name in French.
     */
    public String getNameFr() {
        return constituent.nameFr();
    }

    /**
     * Gets the nutrient name in English.
     */
    public String getNameEn() {
        return constituent.nameEn();
    }

    /**
     * Gets the nutrient code.
     */
    public String getCode() {
        return constituent.code();
    }

    /**
     * Gets the INFOODS code if available.
     */
    public Optional<String> getInfoodsCode() {
        return constituent.infoodsCode();
    }

    /**
     * Returns true if this nutrient has a value.
     */
    public boolean hasValue() {
        return value.isPresent();
    }

    /**
     * Gets the value or 0.0 if not present.
     */
    public double getValueOrZero() {
        return value.orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("%s: %s",
            getNameEn(),
            value.map(v -> String.format("%.2f", v)).orElse("N/A")
        );
    }
}
