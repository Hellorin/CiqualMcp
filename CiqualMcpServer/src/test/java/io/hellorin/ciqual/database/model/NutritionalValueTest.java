package io.hellorin.ciqual.database.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class NutritionalValueTest {

    private final Constituent constituent = new Constituent("31000", "Glucides", "Carbohydrate", Optional.of("CHOAVLDF"));

    @Test
    void withValue_reportsHasValueAndDelegatesToConstituent() {
        NutritionalValue value = new NutritionalValue(
            constituent, Optional.of(12.5), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );

        assertThat(value.hasValue()).isTrue();
        assertThat(value.getValueOrZero()).isEqualTo(12.5);
        assertThat(value.getNameFr()).isEqualTo("Glucides");
        assertThat(value.getNameEn()).isEqualTo("Carbohydrate");
        assertThat(value.getCode()).isEqualTo("31000");
        assertThat(value.getInfoodsCode()).contains("CHOAVLDF");
        assertThat(value.toString()).isEqualTo("Carbohydrate: 12.50");
    }

    @Test
    void withoutValue_reportsNoValueAndZeroDefault() {
        NutritionalValue value = new NutritionalValue(
            constituent, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );

        assertThat(value.hasValue()).isFalse();
        assertThat(value.getValueOrZero()).isZero();
        assertThat(value.toString()).isEqualTo("Carbohydrate: N/A");
    }
}
