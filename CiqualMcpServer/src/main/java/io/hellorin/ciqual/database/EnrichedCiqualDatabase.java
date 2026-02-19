package io.hellorin.ciqual.database;

import io.hellorin.ciqual.database.model.AlimentGroup;
import io.hellorin.ciqual.database.model.EnrichedAliment;
import io.hellorin.ciqual.database.model.Source;
import io.hellorin.ciqual.search.SemanticFoodSearch;

import java.util.*;

/**
 * Enriched in-memory representation of the Ciqual database.
 * Provides easy access to food items with all their nutritional information.
 */
public class EnrichedCiqualDatabase {
    private final Map<String, EnrichedAliment> alimentsByCode;
    private final Map<String, AlimentGroup> groupsByCode;
    private final Map<String, Source> sourcesByCode;

    public EnrichedCiqualDatabase(
        Map<String, EnrichedAliment> alimentsByCode,
        Map<String, AlimentGroup> groupsByCode,
        Map<String, Source> sourcesByCode
    ) {
        this.alimentsByCode = new HashMap<>(alimentsByCode);
        this.groupsByCode = new HashMap<>(groupsByCode);
        this.sourcesByCode = new HashMap<>(sourcesByCode);
    }

    /**
     * Performs semantic search with custom parameters.
     *
     * @param query The search query
     * @param maxResults Maximum number of results
     * @param minScore Minimum relevance score (0.0 to 1.0)
     * @return List of search results sorted by relevance
     */
    public List<SemanticFoodSearch.SearchResult> semanticSearch(String query, int maxResults, double minScore) {
        SemanticFoodSearch searcher = new SemanticFoodSearch(alimentsByCode.values());
        return searcher.search(query, maxResults, minScore);
    }


    /**
     * Gets database statistics.
     */
    public int getAlimentCount() {
        return alimentsByCode.size();
    }

    public int getGroupCount() {
        return groupsByCode.size();
    }

    public int getSourceCount() {
        return sourcesByCode.size();
    }

    @Override
    public String toString() {
        return String.format(
            "EnrichedCiqualDatabase{aliments=%d, groups=%d, sources=%d}",
            getAlimentCount(), getGroupCount(), getSourceCount()
        );
    }
}
