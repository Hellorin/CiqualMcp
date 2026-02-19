package io.hellorin.ciqual.database.model;

import java.util.*;

/**
 * Enriched aliment that contains all its nutritional information.
 * This is the main class for querying food data.
 */
public class EnrichedAliment {
    private final String code;
    private final String nameFr;
    private final String nameEn;
    private final Optional<String> nameSci;
    private final double jonesFactor;
    private final AlimentGroup group;
    private final Map<String, NutritionalValue> nutrients;

    public EnrichedAliment(String code, String nameFr, String nameEn, Optional<String> nameSci,
                          double jonesFactor, AlimentGroup group,
                          Map<String, NutritionalValue> nutrients) {
        this.code = code;
        this.nameFr = nameFr;
        this.nameEn = nameEn;
        this.nameSci = nameSci;
        this.jonesFactor = jonesFactor;
        this.group = group;
        this.nutrients = new HashMap<>(nutrients);
    }

    // Basic getters
    public String getCode() { return code; }
    public String getNameFr() { return nameFr; }
    public String getNameEn() { return nameEn; }
    public Optional<String> getNameSci() { return nameSci; }
    public double getJonesFactor() { return jonesFactor; }
    public AlimentGroup getGroup() { return group; }

    /**
     * Gets a specific nutrient by its code.
     */
    public Optional<NutritionalValue> getNutrient(String constituentCode) {
        return Optional.ofNullable(nutrients.get(constituentCode));
    }

    /**
     * Gets all nutrients.
     */
    public Map<String, NutritionalValue> getAllNutrients() {
        return Collections.unmodifiableMap(nutrients);
    }

    /**
     * Gets macronutrients (carbs, fat, protein, fiber, water, alcohol).
     */
    public Macronutrients getMacros() {
        double carbs = getNutrientValue("31000");      // Glucides
        double fat = getNutrientValue("40000");        // Lipides
        double protein = getNutrientValue("25000");    // Protéines
        double fiber = getNutrientValue("34100");      // Fibres
        double water = getNutrientValue("400");        // Eau
        double alcohol = getNutrientValue("60000");    // Alcool

        return new Macronutrients(carbs, fat, protein, fiber, water, alcohol);
    }

    /**
     * Gets energy in kcal per 100g.
     */
    public Optional<Double> getEnergyKcal() {
        return getNutrient("328").flatMap(NutritionalValue::value);
    }

    /**
     * Gets energy in kJ per 100g.
     */
    public Optional<Double> getEnergyKj() {
        return getNutrient("327").flatMap(NutritionalValue::value);
    }

    /**
     * Gets all vitamins.
     */
    public List<NutritionalValue> getVitamins() {
        return nutrients.values().stream()
            .filter(nv -> {
                String code = nv.getCode();
                return code.startsWith("5") && nv.hasValue();
            })
            .toList();
    }

    /**
     * Gets all minerals.
     */
    public List<NutritionalValue> getMinerals() {
        return nutrients.values().stream()
            .filter(nv -> {
                String code = nv.getCode();
                return code.startsWith("10") && nv.hasValue();
            })
            .toList();
    }

    /**
     * Gets fatty acid breakdown.
     */
    public Map<String, Double> getFattyAcids() {
        Map<String, Double> fattyAcids = new HashMap<>();
        fattyAcids.put("Saturated", getNutrientValue("40302"));
        fattyAcids.put("Monounsaturated", getNutrientValue("40303"));
        fattyAcids.put("Polyunsaturated", getNutrientValue("40304"));
        return fattyAcids;
    }

    /**
     * Gets sugar content.
     */
    public Optional<Double> getSugars() {
        return getNutrient("32000").flatMap(NutritionalValue::value);
    }

    /**
     * Gets salt content.
     */
    public Optional<Double> getSalt() {
        return getNutrient("10004").flatMap(NutritionalValue::value);
    }

    /**
     * Helper method to get a nutrient value or 0.
     */
    private double getNutrientValue(String code) {
        return getNutrient(code)
            .flatMap(NutritionalValue::value)
            .orElse(0.0);
    }

    /**
     * Searches for nutrients by name (case-insensitive, in English).
     */
    public List<NutritionalValue> searchNutrientsByName(String searchTerm) {
        String search = searchTerm.toLowerCase();
        return nutrients.values().stream()
            .filter(nv -> nv.getNameEn().toLowerCase().contains(search) ||
                         nv.getNameFr().toLowerCase().contains(search))
            .filter(NutritionalValue::hasValue)
            .toList();
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Code: %s, Group: %s",
            nameEn, nameFr, code, group.groupNameEn());
    }
}
