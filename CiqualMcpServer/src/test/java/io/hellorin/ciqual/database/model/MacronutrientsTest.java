package io.hellorin.ciqual.database.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class MacronutrientsTest {

    @Test
    void getCalories_appliesFourFourNineSevenRule() {
        Macronutrients macros = new Macronutrients(10.0, 5.0, 20.0, 2.0, 60.0, 1.0);

        // (10*4) + (20*4) + (5*9) + (1*7) = 40 + 80 + 45 + 7 = 172
        assertThat(macros.getCalories()).isEqualTo(172.0);
    }

    @Test
    void percentages_sumToOneHundred() {
        Macronutrients macros = new Macronutrients(10.0, 5.0, 20.0, 2.0, 60.0, 0.0);

        double sum = macros.getCarbsPercentage() + macros.getProteinPercentage() + macros.getFatPercentage();

        assertThat(sum).isCloseTo(100.0, within(0.01));
    }

    @Test
    void percentages_zeroCalories_returnZero() {
        Macronutrients macros = new Macronutrients(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        assertThat(macros.getCalories()).isZero();
        assertThat(macros.getCarbsPercentage()).isZero();
        assertThat(macros.getProteinPercentage()).isZero();
        assertThat(macros.getFatPercentage()).isZero();
    }

    @Test
    void toString_includesCaloriesAndMacros() {
        Macronutrients macros = new Macronutrients(10.0, 5.0, 20.0, 2.0, 60.0, 0.0);

        assertThat(macros.toString())
            .contains("Carbs=10.0g")
            .contains("Fat=5.0g")
            .contains("Protein=20.0g")
            .contains("165 kcal");
    }
}
