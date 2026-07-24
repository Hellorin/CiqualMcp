package io.hellorin.ciqual.database.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConstituentTest {

    @Test
    void fromXml_trimsFieldsAndKeepsInfoodsCode() {
        Constituent constituent = Constituent.fromXml(" 31000 ", " Glucides ", " Carbohydrate ", " CHOAVLDF ");

        assertThat(constituent.code()).isEqualTo("31000");
        assertThat(constituent.nameFr()).isEqualTo("Glucides");
        assertThat(constituent.nameEn()).isEqualTo("Carbohydrate");
        assertThat(constituent.infoodsCode()).contains("CHOAVLDF");
    }

    @Test
    void fromXml_blankInfoodsCode_returnsEmptyOptional() {
        Constituent constituent = Constituent.fromXml("31000", "Glucides", "Carbohydrate", " ");

        assertThat(constituent.infoodsCode()).isEmpty();
    }

    @Test
    void constructor_blankCode_throws() {
        assertThatThrownBy(() -> new Constituent("", "Glucides", "Carbohydrate", Optional.empty()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_blankNameEn_throws() {
        assertThatThrownBy(() -> new Constituent("31000", "Glucides", " ", Optional.empty()))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
