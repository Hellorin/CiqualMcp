package io.hellorin.ciqual.database.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlimentGroupTest {

    @Test
    void fromXml_trimsAndParsesFields() {
        AlimentGroup group = AlimentGroup.fromXml(
            " 01 ", " Viandes ", " Meat ",
            " 0101 ", " Volailles ", " Poultry ",
            " 010101 ", " Poulet ", " Chicken "
        );

        assertThat(group.groupCode()).isEqualTo("01");
        assertThat(group.groupNameFr()).isEqualTo("Viandes");
        assertThat(group.groupNameEn()).isEqualTo("Meat");
        assertThat(group.subgroupCode()).isEqualTo("0101");
        assertThat(group.subgroupNameFr()).isEqualTo("Volailles");
        assertThat(group.subgroupNameEn()).isEqualTo("Poultry");
        assertThat(group.subSubgroupCode()).contains("010101");
        assertThat(group.subSubgroupNameFr()).contains("Poulet");
        assertThat(group.subSubgroupNameEn()).contains("Chicken");
    }

    @Test
    void fromXml_subSubgroupCodeAllZeros_returnsEmptyOptional() {
        AlimentGroup group = AlimentGroup.fromXml(
            "01", "Viandes", "Meat", "0101", "Volailles", "Poultry",
            "000000", "-", "-"
        );

        assertThat(group.subSubgroupCode()).isEmpty();
    }

    @Test
    void fromXml_subSubgroupNameDash_returnsEmptyOptional() {
        AlimentGroup group = AlimentGroup.fromXml(
            "01", "Viandes", "Meat", "0101", "Volailles", "Poultry",
            "010101", "-", "-"
        );

        assertThat(group.subSubgroupNameFr()).isEmpty();
        assertThat(group.subSubgroupNameEn()).isEmpty();
    }

    @Test
    void fromXml_subSubgroupCodeBlank_returnsEmptyOptional() {
        AlimentGroup group = AlimentGroup.fromXml(
            "01", "Viandes", "Meat", "0101", "Volailles", "Poultry",
            " ", " Poulet ", " Chicken "
        );

        assertThat(group.subSubgroupCode()).isEmpty();
    }

    @Test
    void constructor_blankGroupCode_throws() {
        assertThatThrownBy(() -> new AlimentGroup(
            "", "Viandes", "Meat", "0101", "Volailles", "Poultry",
            Optional.empty(), Optional.empty(), Optional.empty()
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_blankSubgroupNameEn_throws() {
        assertThatThrownBy(() -> new AlimentGroup(
            "01", "Viandes", "Meat", "0101", "Volailles", " ",
            Optional.empty(), Optional.empty(), Optional.empty()
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
