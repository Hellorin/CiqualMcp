package io.hellorin.ciqual.database.model;

/**
 * Represents the macronutrients (carbs, fat, protein) of a food item.
 */
public record Macronutrients(
    double carbohydrates,  // g/100g
    double fat,            // g/100g
    double protein,        // g/100g
    double fiber,          // g/100g
    double water,          // g/100g
    double alcohol         // g/100g
) {

    /**
     * Gets total calories using the 4-4-9-7 rule:
     * - Carbs: 4 kcal/g
     * - Protein: 4 kcal/g
     * - Fat: 9 kcal/g
     * - Alcohol: 7 kcal/g
     */
    public double getCalories() {
        return (carbohydrates * 4.0) + (protein * 4.0) + (fat * 9.0) + (alcohol * 7.0);
    }

    /**
     * Gets the percentage of calories from carbohydrates.
     */
    public double getCarbsPercentage() {
        double totalCals = getCalories();
        return totalCals > 0 ? (carbohydrates * 4.0 / totalCals) * 100 : 0;
    }

    /**
     * Gets the percentage of calories from protein.
     */
    public double getProteinPercentage() {
        double totalCals = getCalories();
        return totalCals > 0 ? (protein * 4.0 / totalCals) * 100 : 0;
    }

    /**
     * Gets the percentage of calories from fat.
     */
    public double getFatPercentage() {
        double totalCals = getCalories();
        return totalCals > 0 ? (fat * 9.0 / totalCals) * 100 : 0;
    }

    @Override
    public String toString() {
        return String.format(
            "Macros per 100g: Carbs=%.1fg, Fat=%.1fg, Protein=%.1fg, Fiber=%.1fg, Water=%.1fg, Alcohol=%.1fg (%.0f kcal)",
            carbohydrates, fat, protein, fiber, water, alcohol, getCalories()
        );
    }
}
