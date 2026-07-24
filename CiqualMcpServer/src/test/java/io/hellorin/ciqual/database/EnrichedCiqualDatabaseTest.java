package io.hellorin.ciqual.database;

import io.hellorin.ciqual.database.model.EnrichedAliment;
import io.hellorin.ciqual.database.model.Source;
import io.hellorin.ciqual.search.SemanticFoodSearch;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.hellorin.ciqual.testsupport.AlimentFixtures.aliment;
import static io.hellorin.ciqual.testsupport.AlimentFixtures.group;
import static org.assertj.core.api.Assertions.assertThat;

class EnrichedCiqualDatabaseTest {

    @Test
    void countsReflectSuppliedMaps() {
        EnrichedAliment chicken = aliment("1", "Poulet", "Chicken breast", group("Meat", "Poultry"), Map.of());
        EnrichedAliment apple = aliment("2", "Pomme", "Apple", group("Fruit", "Fresh fruit"), Map.of());

        EnrichedCiqualDatabase database = new EnrichedCiqualDatabase(
            Map.of("1", chicken, "2", apple),
            Map.of("g1", group("Meat", "Poultry")),
            Map.of("s1", new Source("s1", Optional.of("Ciqual")))
        );

        assertThat(database.getAlimentCount()).isEqualTo(2);
        assertThat(database.getGroupCount()).isEqualTo(1);
        assertThat(database.getSourceCount()).isEqualTo(1);
    }

    @Test
    void semanticSearch_delegatesToSemanticFoodSearchOverAllAliments() {
        EnrichedAliment chicken = aliment("1", "Poulet", "Chicken breast", group("Meat", "Poultry"), Map.of());
        EnrichedAliment apple = aliment("2", "Pomme", "Apple", group("Fruit", "Fresh fruit"), Map.of());

        EnrichedCiqualDatabase database = new EnrichedCiqualDatabase(
            Map.of("1", chicken, "2", apple), Map.of(), Map.of()
        );

        List<SemanticFoodSearch.SearchResult> results = database.semanticSearch("chicken", 5, 0.1);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).aliment().getCode()).isEqualTo("1");
    }

    @Test
    void toString_includesCounts() {
        EnrichedCiqualDatabase database = new EnrichedCiqualDatabase(Map.of(), Map.of(), Map.of());

        assertThat(database.toString()).isEqualTo("EnrichedCiqualDatabase{aliments=0, groups=0, sources=0}");
    }
}
