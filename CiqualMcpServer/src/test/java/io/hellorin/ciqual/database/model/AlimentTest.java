package io.hellorin.ciqual.database.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlimentTest {

    @Test
    void fromXml_trimsAndParsesFields() {
        Aliment aliment = Aliment.fromXml(
            " 12345 ", " Poulet ", " Chicken ", " Gallus gallus ",
            " 01 ", " 0101 ", " 010101 ", " 1.0 "
        );

        assertThat(aliment.code()).isEqualTo("12345");
        assertThat(aliment.nameFr()).isEqualTo("Poulet");
        assertThat(aliment.nameEn()).isEqualTo("Chicken");
        assertThat(aliment.nameSci()).isPresent().contains("Gallus gallus");
        assertThat(aliment.groupCode()).isEqualTo("01");
        assertThat(aliment.subgroupCode()).isEqualTo("0101");
        assertThat(aliment.subSubgroupCode()).isEqualTo("010101");
        assertThat(aliment.jonesFactor()).isEqualTo(1.0);
    }

    @Test
    void fromXml_blankScientificName_returnsEmptyOptional() {
        Aliment aliment = Aliment.fromXml("1", "Nom", "Name", "  ", "01", "0101", "010101", "1.0");

        assertThat(aliment.nameSci()).isEmpty();
    }

    @Test
    void fromXml_nullScientificName_returnsEmptyOptional() {
        Aliment aliment = Aliment.fromXml("1", "Nom", "Name", null, "01", "0101", "010101", "1.0");

        assertThat(aliment.nameSci()).isEmpty();
    }

    @Test
    void constructor_blankCode_throws() {
        assertThatThrownBy(() -> new Aliment("", "Nom", "Name", Optional.empty(), "01", "0101", "010101", 1.0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_blankNameFr_throws() {
        assertThatThrownBy(() -> new Aliment("1", " ", "Name", Optional.empty(), "01", "0101", "010101", 1.0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_blankNameEn_throws() {
        assertThatThrownBy(() -> new Aliment("1", "Nom", null, Optional.empty(), "01", "0101", "010101", 1.0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_blankGroupCode_throws() {
        assertThatThrownBy(() -> new Aliment("1", "Nom", "Name", Optional.empty(), "", "0101", "010101", 1.0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_blankSubgroupCode_throws() {
        assertThatThrownBy(() -> new Aliment("1", "Nom", "Name", Optional.empty(), "01", "", "010101", 1.0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_blankSubSubgroupCode_throws() {
        assertThatThrownBy(() -> new Aliment("1", "Nom", "Name", Optional.empty(), "01", "0101", "", 1.0))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
