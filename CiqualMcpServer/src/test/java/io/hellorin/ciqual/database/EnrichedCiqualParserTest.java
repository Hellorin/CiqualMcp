package io.hellorin.ciqual.database;

import io.hellorin.ciqual.database.model.EnrichedAliment;
import io.hellorin.ciqual.search.SemanticFoodSearch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnrichedCiqualParserTest {

    private static EnrichedCiqualDatabase database;

    @BeforeAll
    static void loadDatabase() throws Exception {
        database = EnrichedCiqualParser.loadDatabase();
    }

    @Test
    void loadDatabase_parsesAllReferenceFiles() {
        assertThat(database.getAlimentCount()).isPositive();
        assertThat(database.getGroupCount()).isPositive();
        assertThat(database.getSourceCount()).isPositive();
    }

    @Test
    void loadDatabase_buildsEnrichedAlimentWithMatchedGroupAndNutrients() {
        EnrichedAliment pastis = database.semanticSearch("Pastis anise-flavoured spirit", 5, 0.1).stream()
            .map(SemanticFoodSearch.SearchResult::aliment)
            .filter(a -> a.getCode().equals("1000"))
            .findFirst()
            .orElseThrow();

        assertThat(pastis.getNameFr()).isEqualTo("Pastis");
        assertThat(pastis.getNameEn()).isEqualTo("Pastis (anise-flavoured spirit)");
        assertThat(pastis.getNameSci()).isEmpty();
        assertThat(pastis.getJonesFactor()).isEqualTo(6.25);

        assertThat(pastis.getGroup().groupCode()).isEqualTo("06");
        assertThat(pastis.getGroup().groupNameEn()).isEqualTo("beverages");
        assertThat(pastis.getGroup().subgroupCode()).isEqualTo("0603");
        assertThat(pastis.getGroup().subSubgroupCode()).contains("060303");
        assertThat(pastis.getGroup().subSubgroupNameEn()).contains("liqueurs and spirits");

        assertThat(pastis.getEnergyKj()).contains(1140.0);
        assertThat(pastis.getEnergyKcal()).contains(274.0);

        assertThat(pastis.getNutrient("327")).hasValueSatisfying(nutrient -> {
            assertThat(nutrient.confidenceCode()).contains("D");
            assertThat(nutrient.sourceCode()).contains("444");
        });
    }
}
