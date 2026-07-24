package io.hellorin.ciqual.tools;

import io.hellorin.ciqual.database.EnrichedCiqualDatabase;
import io.hellorin.ciqual.database.model.EnrichedAliment;
import io.hellorin.ciqual.search.SemanticFoodSearch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static io.hellorin.ciqual.testsupport.AlimentFixtures.aliment;
import static io.hellorin.ciqual.testsupport.AlimentFixtures.group;
import static io.hellorin.ciqual.testsupport.AlimentFixtures.nutrient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodSearchToolsTest {

    @Mock
    private EnrichedCiqualDatabase database;

    @Test
    void searchFood_mapsSemanticSearchResultsToMacroResults() {
        Map<String, io.hellorin.ciqual.database.model.NutritionalValue> nutrients = Map.of(
            "31000", nutrient("31000", 10.0),
            "40000", nutrient("40000", 5.0),
            "25000", nutrient("25000", 20.0)
        );
        EnrichedAliment chicken = aliment("1", "Poulet", "Chicken breast", group("Meat", "Poultry"), nutrients);
        SemanticFoodSearch.SearchResult searchResult = new SemanticFoodSearch.SearchResult(chicken, 0.9, Map.of());

        when(database.semanticSearch("chicken", 5, 0.1)).thenReturn(List.of(searchResult));

        FoodSearchTools tools = new FoodSearchTools(database);
        List<FoodMacroResult> results = tools.searchFood("chicken");

        assertThat(results).hasSize(1);
        FoodMacroResult result = results.get(0);
        assertThat(result.nameFr()).isEqualTo("Poulet");
        assertThat(result.nameEn()).isEqualTo("Chicken breast");
        assertThat(result.carbohydrates()).isEqualTo(10.0);
        assertThat(result.fat()).isEqualTo(5.0);
        assertThat(result.protein()).isEqualTo(20.0);
        // (10*4) + (20*4) + (5*9) = 40 + 80 + 45 = 165
        assertThat(result.caloriesKcal()).isEqualTo(165.0);
    }

    @Test
    void searchFood_noMatches_returnsEmptyList() {
        when(database.semanticSearch(anyString(), anyInt(), anyDouble())).thenReturn(List.of());

        FoodSearchTools tools = new FoodSearchTools(database);
        List<FoodMacroResult> results = tools.searchFood("nonexistent");

        assertThat(results).isEmpty();
    }

    @Test
    void searchFood_delegatesWithFixedSearchParameters() {
        when(database.semanticSearch(anyString(), anyInt(), anyDouble())).thenReturn(List.of());

        FoodSearchTools tools = new FoodSearchTools(database);
        tools.searchFood("pain");

        verify(database).semanticSearch("pain", 5, 0.1);
    }
}
