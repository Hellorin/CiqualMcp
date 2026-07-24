package io.hellorin.ciqual.database.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.hellorin.ciqual.testsupport.AlimentFixtures.aliment;
import static io.hellorin.ciqual.testsupport.AlimentFixtures.group;
import static io.hellorin.ciqual.testsupport.AlimentFixtures.nutrient;
import static org.assertj.core.api.Assertions.assertThat;

class EnrichedAlimentTest {

    @Test
    void getMacros_readsKnownNutrientCodes() {
        Map<String, NutritionalValue> nutrients = new HashMap<>();
        nutrients.put("31000", nutrient("31000", 10.0)); // carbs
        nutrients.put("40000", nutrient("40000", 5.0));  // fat
        nutrients.put("25000", nutrient("25000", 20.0)); // protein
        nutrients.put("34100", nutrient("34100", 2.0));  // fiber
        nutrients.put("400", nutrient("400", 60.0));     // water
        nutrients.put("60000", nutrient("60000", 1.0));  // alcohol

        EnrichedAliment aliment = aliment("1", "Poulet", "Chicken", group("Meat", "Poultry"), nutrients);

        Macronutrients macros = aliment.getMacros();
        assertThat(macros.carbohydrates()).isEqualTo(10.0);
        assertThat(macros.fat()).isEqualTo(5.0);
        assertThat(macros.protein()).isEqualTo(20.0);
        assertThat(macros.fiber()).isEqualTo(2.0);
        assertThat(macros.water()).isEqualTo(60.0);
        assertThat(macros.alcohol()).isEqualTo(1.0);
    }

    @Test
    void getMacros_missingNutrients_defaultToZero() {
        EnrichedAliment aliment = aliment("1", "Eau", "Water", group("Beverages", "Water"), Map.of());

        Macronutrients macros = aliment.getMacros();
        assertThat(macros.carbohydrates()).isZero();
        assertThat(macros.fat()).isZero();
        assertThat(macros.protein()).isZero();
    }

    @Test
    void getEnergy_readsKcalAndKjCodes() {
        Map<String, NutritionalValue> nutrients = Map.of(
            "328", nutrient("328", 250.0),
            "327", nutrient("327", 1046.0)
        );
        EnrichedAliment aliment = aliment("1", "Poulet", "Chicken", group("Meat", "Poultry"), nutrients);

        assertThat(aliment.getEnergyKcal()).contains(250.0);
        assertThat(aliment.getEnergyKj()).contains(1046.0);
    }

    @Test
    void getEnergy_whenAbsent_isEmpty() {
        EnrichedAliment aliment = aliment("1", "Poulet", "Chicken", group("Meat", "Poultry"), Map.of());

        assertThat(aliment.getEnergyKcal()).isEmpty();
        assertThat(aliment.getEnergyKj()).isEmpty();
    }

    @Test
    void getVitamins_returnsOnlyValuedNutrientsWithCodeStartingWithFive() {
        Map<String, NutritionalValue> nutrients = new HashMap<>();
        nutrients.put("54100", nutrient("54100", 1.5));               // vitamin, has value
        nutrients.put("54200", new NutritionalValue(                  // vitamin, no value
            new Constituent("54200", "Vitamine X", "Vitamin X", Optional.empty()),
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        ));
        nutrients.put("31000", nutrient("31000", 10.0));              // not a vitamin code

        EnrichedAliment aliment = aliment("1", "Poulet", "Chicken", group("Meat", "Poultry"), nutrients);

        assertThat(aliment.getVitamins())
            .extracting(NutritionalValue::getCode)
            .containsExactly("54100");
    }

    @Test
    void getMinerals_returnsOnlyValuedNutrientsWithCodeStartingWithTen() {
        Map<String, NutritionalValue> nutrients = new HashMap<>();
        nutrients.put("10004", nutrient("10004", 0.5)); // mineral (salt)
        nutrients.put("31000", nutrient("31000", 10.0)); // not a mineral code

        EnrichedAliment aliment = aliment("1", "Poulet", "Chicken", group("Meat", "Poultry"), nutrients);

        assertThat(aliment.getMinerals())
            .extracting(NutritionalValue::getCode)
            .containsExactly("10004");
    }

    @Test
    void getFattyAcids_readsKnownCodesAndDefaultsMissingToZero() {
        Map<String, NutritionalValue> nutrients = Map.of(
            "40302", nutrient("40302", 2.0),
            "40304", nutrient("40304", 3.0)
        );
        EnrichedAliment aliment = aliment("1", "Poulet", "Chicken", group("Meat", "Poultry"), nutrients);

        Map<String, Double> fattyAcids = aliment.getFattyAcids();
        assertThat(fattyAcids.get("Saturated")).isEqualTo(2.0);
        assertThat(fattyAcids.get("Monounsaturated")).isEqualTo(0.0);
        assertThat(fattyAcids.get("Polyunsaturated")).isEqualTo(3.0);
    }

    @Test
    void getSugarsAndSalt_readKnownCodes() {
        Map<String, NutritionalValue> nutrients = Map.of(
            "32000", nutrient("32000", 5.0),
            "10004", nutrient("10004", 1.2)
        );
        EnrichedAliment aliment = aliment("1", "Poulet", "Chicken", group("Meat", "Poultry"), nutrients);

        assertThat(aliment.getSugars()).contains(5.0);
        assertThat(aliment.getSalt()).contains(1.2);
    }

    @Test
    void searchNutrientsByName_isCaseInsensitiveAcrossBothLanguages_andRequiresValue() {
        Map<String, NutritionalValue> nutrients = new HashMap<>();
        nutrients.put("31000", nutrient("31000", "Glucides", "Carbohydrate", 10.0));
        nutrients.put("40000", nutrient("40000", "Lipides", "Fat", 5.0));
        nutrients.put("99999", new NutritionalValue( // matches name but has no value
            new Constituent("99999", "Glucides rapides", "Fast carbohydrate", Optional.empty()),
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        ));

        EnrichedAliment aliment = aliment("1", "Poulet", "Chicken", group("Meat", "Poultry"), nutrients);

        assertThat(aliment.searchNutrientsByName("CARBO"))
            .extracting(NutritionalValue::getCode)
            .containsExactly("31000");
        assertThat(aliment.searchNutrientsByName("glucides"))
            .extracting(NutritionalValue::getCode)
            .containsExactly("31000");
    }

    @Test
    void toString_includesNamesCodeAndGroup() {
        EnrichedAliment aliment = aliment("12345", "Poulet", "Chicken", group("Meat", "Poultry"), Map.of());

        assertThat(aliment.toString())
            .contains("Chicken")
            .contains("Poulet")
            .contains("12345")
            .contains("Meat");
    }
}
