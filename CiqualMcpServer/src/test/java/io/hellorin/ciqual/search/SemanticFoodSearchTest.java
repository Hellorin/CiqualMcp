package io.hellorin.ciqual.search;

import io.hellorin.ciqual.database.model.EnrichedAliment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.hellorin.ciqual.testsupport.AlimentFixtures.aliment;
import static io.hellorin.ciqual.testsupport.AlimentFixtures.group;
import static org.assertj.core.api.Assertions.assertThat;

class SemanticFoodSearchTest {

    private final EnrichedAliment chicken = aliment(
        "1", "Poulet", "Chicken breast", group("Meat", "Poultry"), Map.of()
    );
    private final EnrichedAliment apple = aliment(
        "2", "Pomme", "Apple", group("Fruit", "Fresh fruit"), Map.of()
    );
    private final EnrichedAliment rice = aliment(
        "3", "Riz blanc", "White rice", group("Grain products", "Cereals"), Map.of()
    );

    @Test
    void search_exactNameMatch_scoresHighestAndIsFirst() {
        SemanticFoodSearch search = new SemanticFoodSearch(List.of(chicken, apple, rice));

        List<SemanticFoodSearch.SearchResult> results = search.search("poulet", 5, 0.1);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).aliment().getCode()).isEqualTo("1");
        assertThat(results.get(0).score()).isGreaterThan(0.3);
    }

    @Test
    void search_unrelatedQuery_returnsNoResultsAboveMinScore() {
        SemanticFoodSearch search = new SemanticFoodSearch(List.of(chicken, apple, rice));

        List<SemanticFoodSearch.SearchResult> results = search.search("automobile", 5, 0.2);

        assertThat(results).isEmpty();
    }

    @Test
    void search_accentInsensitive_matchesNormalizedName() {
        EnrichedAliment coffee = aliment("4", "Café", "Coffee", group("Beverages", "Hot drinks"), Map.of());
        SemanticFoodSearch search = new SemanticFoodSearch(List.of(coffee));

        List<SemanticFoodSearch.SearchResult> results = search.search("cafe", 5, 0.1);

        assertThat(results).extracting(r -> r.aliment().getCode()).containsExactly("4");
    }

    @Test
    void search_synonymOnlyMatch_findsAlimentViaGroupSynonym() {
        // Name and group text share no literal token with the query "chicken";
        // the match can only come from the English->French synonym expansion on the subgroup.
        EnrichedAliment breadedCutlet = aliment(
            "5", "Escalope panee", "Breaded cutlet", group("Meat", "Poultry"), Map.of()
        );
        SemanticFoodSearch search = new SemanticFoodSearch(List.of(breadedCutlet));

        List<SemanticFoodSearch.SearchResult> lowThreshold = search.search("chicken", 5, 0.05);
        List<SemanticFoodSearch.SearchResult> highThreshold = search.search("chicken", 5, 0.5);

        assertThat(lowThreshold).extracting(r -> r.aliment().getCode()).containsExactly("5");
        assertThat(highThreshold).isEmpty();
    }

    @Test
    void search_maxResults_limitsAndOrdersByScoreDescending() {
        EnrichedAliment fruit1 = aliment("10", "Fruit un", "Fruit one", group("Fruit", "Fresh fruit"), Map.of());
        EnrichedAliment fruit2 = aliment("11", "Fruit deux", "Fruit two", group("Fruit", "Fresh fruit"), Map.of());
        EnrichedAliment fruit3 = aliment("12", "Fruit trois", "Fruit three", group("Fruit", "Fresh fruit"), Map.of());

        SemanticFoodSearch search = new SemanticFoodSearch(List.of(fruit1, fruit2, fruit3));

        List<SemanticFoodSearch.SearchResult> results = search.search("fruit", 2, 0.05);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).score()).isGreaterThanOrEqualTo(results.get(1).score());
    }

    @Test
    void search_defaultOverload_usesDefaultMaxResultsAndMinScore() {
        SemanticFoodSearch search = new SemanticFoodSearch(List.of(chicken, apple, rice));

        List<SemanticFoodSearch.SearchResult> results = search.search("poulet");

        assertThat(results).extracting(r -> r.aliment().getCode()).containsExactly("1");
    }

    @Test
    void searchResult_getExplanation_listsMatchedFieldsByDescendingScore() {
        SemanticFoodSearch search = new SemanticFoodSearch(List.of(chicken));

        SemanticFoodSearch.SearchResult result = search.search("poulet", 5, 0.1).get(0);

        assertThat(result.getExplanation()).contains("nameFr");
    }
}
