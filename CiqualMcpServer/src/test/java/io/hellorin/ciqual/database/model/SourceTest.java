package io.hellorin.ciqual.database.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SourceTest {

    @Test
    void fromXml_trimsCitation() {
        Source source = Source.fromXml(" SRC1 ", " Ciqual 2020 ");

        assertThat(source.code()).isEqualTo("SRC1");
        assertThat(source.citation()).contains("Ciqual 2020");
    }

    @Test
    void fromXml_blankCitation_returnsEmptyOptional() {
        Source source = Source.fromXml("SRC1", " ");

        assertThat(source.citation()).isEmpty();
    }

    @Test
    void constructor_blankCode_throws() {
        assertThatThrownBy(() -> new Source("", Optional.empty()))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
