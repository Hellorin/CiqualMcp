package io.hellorin.ciqual.database.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompositionTest {

    @Test
    void fromXml_parsesFrenchDecimalCommaAsDot() {
        Composition composition = Composition.fromXml("1", "31000", "12,5", "10,0", "15,0", "A", "SRC1");

        assertThat(composition.teneur()).contains(12.5);
        assertThat(composition.min()).contains(10.0);
        assertThat(composition.max()).contains(15.0);
    }

    @Test
    void fromXml_dashTeneur_returnsEmptyOptional() {
        Composition composition = Composition.fromXml("1", "31000", "-", "-", "-", "A", "SRC1");

        assertThat(composition.teneur()).isEmpty();
        assertThat(composition.min()).isEmpty();
        assertThat(composition.max()).isEmpty();
    }

    @Test
    void fromXml_blankTeneur_returnsEmptyOptional() {
        Composition composition = Composition.fromXml("1", "31000", " ", null, "", "A", "SRC1");

        assertThat(composition.teneur()).isEmpty();
        assertThat(composition.min()).isEmpty();
        assertThat(composition.max()).isEmpty();
    }

    @Test
    void fromXml_nonNumericTeneur_returnsEmptyOptional() {
        Composition composition = Composition.fromXml("1", "31000", "traces", "10", "15", "A", "SRC1");

        assertThat(composition.teneur()).isEmpty();
    }

    @Test
    void fromXml_dotCodeConfiance_returnsEmptyOptional() {
        Composition composition = Composition.fromXml("1", "31000", "12", "10", "15", ".", "SRC1");

        assertThat(composition.codeConfiance()).isEmpty();
    }

    @Test
    void fromXml_presentCodeConfianceAndSource_areTrimmed() {
        Composition composition = Composition.fromXml("1", "31000", "12", "10", "15", " A ", " SRC1 ");

        assertThat(composition.codeConfiance()).contains("A");
        assertThat(composition.sourceCode()).contains("SRC1");
    }

    @Test
    void constructor_blankAlimentCode_throws() {
        assertThatThrownBy(() -> Composition.fromXml("", "31000", "12", "10", "15", "A", "SRC1"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_blankConstituentCode_throws() {
        assertThatThrownBy(() -> Composition.fromXml("1", " ", "12", "10", "15", "A", "SRC1"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
