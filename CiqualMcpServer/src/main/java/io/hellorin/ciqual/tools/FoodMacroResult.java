package io.hellorin.ciqual.tools;

public record FoodMacroResult(
        String nameFr,
        String nameEn,
        double carbohydrates,
        double fat,
        double protein,
        double caloriesKcal
) {}
