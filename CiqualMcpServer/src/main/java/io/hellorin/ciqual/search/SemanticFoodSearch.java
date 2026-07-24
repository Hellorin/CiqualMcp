package io.hellorin.ciqual.search;

import io.hellorin.ciqual.database.model.EnrichedAliment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Semantic search engine for food items.
 * Supports fuzzy matching, synonyms, and multi-field weighted search.
 */
public class SemanticFoodSearch {

    private final Collection<EnrichedAliment> aliments;
    private final Map<String, Set<String>> synonyms;

    public SemanticFoodSearch(Collection<EnrichedAliment> aliments) {
        this.aliments = aliments;
        this.synonyms = buildSynonymMap();
    }

    /**
     * Performs semantic search with fuzzy matching and synonym expansion.
     *
     * @param query The search query
     * @param maxResults Maximum number of results to return
     * @param minScore Minimum score threshold (0.0 to 1.0)
     * @return List of search results sorted by relevance score
     */
    public List<SearchResult> search(String query, int maxResults, double minScore) {
        String normalizedQuery = normalize(query);
        Set<String> expandedTerms = expandWithSynonyms(normalizedQuery);

        return aliments.stream()
            .map(aliment -> scoreAliment(aliment, normalizedQuery, expandedTerms))
            .filter(result -> result.score() >= minScore)
            .sorted(Comparator.comparingDouble(SearchResult::score).reversed())
            .limit(maxResults)
            .toList();
    }

    /**
     * Simplified search with default parameters.
     */
    public List<SearchResult> search(String query) {
        return search(query, 20, 0.3);
    }

    /**
     * Scores an aliment against the search query.
     */
    private SearchResult scoreAliment(EnrichedAliment aliment, String query, Set<String> expandedTerms) {
        Map<String, Double> matchDetails = new HashMap<>();

        // French name (weight: 1.0)
        String nameFr = normalize(aliment.getNameFr());
        double nameFrScore = calculateFieldScore(nameFr, query, expandedTerms);
        double score = nameFrScore * 1.0;
        if (nameFrScore > 0) matchDetails.put("nameFr", nameFrScore);

        // English name (weight: 1.0)
        String nameEn = normalize(aliment.getNameEn());
        double nameEnScore = calculateFieldScore(nameEn, query, expandedTerms);
        score += nameEnScore * 1.0;
        if (nameEnScore > 0) matchDetails.put("nameEn", nameEnScore);

        // Scientific name (weight: 0.7)
        Optional<String> nameSciOpt = aliment.getNameSci();
        if (nameSciOpt.isPresent()) {
            String nameSci = normalize(nameSciOpt.get());
            double nameSciScore = calculateFieldScore(nameSci, query, expandedTerms);
            score += nameSciScore * 0.7;
            if (nameSciScore > 0) matchDetails.put("nameSci", nameSciScore);
        }

        // Group names (weight: 0.5)
        String groupName = normalize(aliment.getGroup().groupNameEn());
        double groupScore = calculateFieldScore(groupName, query, expandedTerms);
        score += groupScore * 0.5;
        if (groupScore > 0) matchDetails.put("group", groupScore);

        // Subgroup (weight: 0.6)
        String subgroupName = normalize(aliment.getGroup().subgroupNameEn());
        double subgroupScore = calculateFieldScore(subgroupName, query, expandedTerms);
        score += subgroupScore * 0.6;
        if (subgroupScore > 0) matchDetails.put("subgroup", subgroupScore);

        // Sub-subgroup (weight: 0.6)
        Optional<String> subSubgroupNameOpt = aliment.getGroup().subSubgroupNameEn();
        if (subSubgroupNameOpt.isPresent()) {
            String subSubgroupName = normalize(subSubgroupNameOpt.get());
            double subSubgroupScore = calculateFieldScore(subSubgroupName, query, expandedTerms);
            score += subSubgroupScore * 0.6;
            if (subSubgroupScore > 0) matchDetails.put("subSubgroup", subSubgroupScore);
        }

        // Normalize score to 0-1 range (max possible weight sum = 4.3)
        score = Math.min(score / 4.3, 1.0);

        return new SearchResult(aliment, score, matchDetails);
    }

    /**
     * Calculates score for a single field.
     */
    private double calculateFieldScore(String field, String query, Set<String> expandedTerms) {
        double maxScore = 0.0;

        // Exact match
        if (field.equals(query)) {
            return 1.0;
        }

        // Contains exact query
        if (field.contains(query)) {
            maxScore = Math.max(maxScore, 0.9);
        }

        // Word-level matching
        String[] fieldWords = field.split("\\s+");
        String[] queryWords = query.split("\\s+");

        for (String queryWord : queryWords) {
            for (String fieldWord : fieldWords) {
                // Exact word match
                if (fieldWord.equals(queryWord)) {
                    maxScore = Math.max(maxScore, 0.85);
                }

                // Starts with
                if (fieldWord.startsWith(queryWord) || queryWord.startsWith(fieldWord)) {
                    maxScore = Math.max(maxScore, 0.75);
                }

                // Fuzzy match (Levenshtein)
                double similarity = calculateSimilarity(fieldWord, queryWord);
                maxScore = Math.max(maxScore, similarity * 0.8);
            }
        }

        // Synonym matching
        for (String synonym : expandedTerms) {
            if (field.contains(synonym)) {
                maxScore = Math.max(maxScore, 0.7);
            }

            for (String fieldWord : fieldWords) {
                if (fieldWord.equals(synonym)) {
                    maxScore = Math.max(maxScore, 0.75);
                }
            }
        }

        return maxScore;
    }

    /**
     * Calculates string similarity using Levenshtein distance.
     * Returns a value between 0.0 (completely different) and 1.0 (identical).
     */
    private double calculateSimilarity(String s1, String s2) {
        int distance = levenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) distance / maxLen);
    }

    /**
     * Calculates Levenshtein distance between two strings.
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Expands query with synonyms.
     */
    private Set<String> expandWithSynonyms(String query) {
        Set<String> expanded = new HashSet<>();
        String[] words = query.split("\\s+");

        for (String word : words) {
            if (synonyms.containsKey(word)) {
                expanded.addAll(synonyms.get(word));
            }
        }

        return expanded;
    }

    /**
     * Normalizes text for comparison.
     */
    private String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase()
            .replaceAll("[àáâãäå]", "a")
            .replaceAll("[èéêë]", "e")
            .replaceAll("[ìíîï]", "i")
            .replaceAll("[òóôõö]", "o")
            .replaceAll("[ùúûü]", "u")
            .replaceAll("[ýÿ]", "y")
            .replaceAll("[ç]", "c")
            .replaceAll("[ñ]", "n")
            .trim();
    }

    /**
     * Builds a synonym map for common food terms.
     * This can be extended or loaded from a configuration file.
     */
    private Map<String, Set<String>> buildSynonymMap() {
        Map<String, Set<String>> map = new HashMap<>();

        // Meat & Poultry
        addSynonyms(map, "chicken", "poultry", "poulet", "volaille");
        addSynonyms(map, "beef", "meat", "boeuf", "viande");
        addSynonyms(map, "pork", "meat", "porc", "viande");
        addSynonyms(map, "lamb", "meat", "agneau", "viande");

        // Dairy
        addSynonyms(map, "milk", "dairy", "lait", "laitier");
        addSynonyms(map, "cheese", "dairy", "fromage", "laitier");
        addSynonyms(map, "yogurt", "dairy", "yaourt", "yoghurt", "laitier");

        // Grains
        addSynonyms(map, "bread", "grain", "pain", "cereale");
        addSynonyms(map, "rice", "grain", "riz", "cereale");
        addSynonyms(map, "pasta", "grain", "pates", "cereale");
        addSynonyms(map, "wheat", "grain", "ble", "cereale");

        // Vegetables
        addSynonyms(map, "vegetable", "veggie", "legume");
        addSynonyms(map, "potato", "vegetable", "pomme de terre", "legume");
        addSynonyms(map, "tomato", "vegetable", "tomate", "legume");
        addSynonyms(map, "carrot", "vegetable", "carotte", "legume");

        // Fruits
        addSynonyms(map, "fruit", "fruits");
        addSynonyms(map, "apple", "fruit", "pomme", "fruits");
        addSynonyms(map, "banana", "fruit", "banane", "fruits");
        addSynonyms(map, "orange", "fruit", "citrus", "fruits");

        // Fish & Seafood
        addSynonyms(map, "fish", "seafood", "poisson", "fruits de mer");
        addSynonyms(map, "salmon", "fish", "saumon", "poisson");
        addSynonyms(map, "tuna", "fish", "thon", "poisson");
        addSynonyms(map, "shrimp", "seafood", "crevette", "fruits de mer");

        // Beverages
        addSynonyms(map, "drink", "beverage", "boisson");
        addSynonyms(map, "juice", "drink", "jus", "boisson");
        addSynonyms(map, "water", "drink", "eau", "boisson");
        addSynonyms(map, "coffee", "drink", "cafe", "boisson");

        // Sweets
        addSynonyms(map, "sugar", "sweet", "sucre");
        addSynonyms(map, "candy", "sweet", "bonbon", "confiserie");
        addSynonyms(map, "chocolate", "sweet", "chocolat");

        return map;
    }

    /**
     * Helper to add bidirectional synonyms.
     */
    private void addSynonyms(Map<String, Set<String>> map, String... words) {
        for (String word : words) {
            String normalized = normalize(word);
            Set<String> synonymSet = map.computeIfAbsent(normalized, k -> new HashSet<>());
            for (String other : words) {
                if (!other.equals(word)) {
                    synonymSet.add(normalize(other));
                }
            }
        }
    }

    /**
     * Search result with relevance score.
     */
    public record SearchResult(
        EnrichedAliment aliment,
        double score,
        Map<String, Double> matchDetails
    ) {
        public String getExplanation() {
            return matchDetails.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(e -> String.format("%s: %.2f", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
        }
    }
}
