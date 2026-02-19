package io.hellorin.ciqual.tools;

import io.hellorin.ciqual.database.EnrichedCiqualDatabase;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodSearchTools {

    private final EnrichedCiqualDatabase database;

    public FoodSearchTools(EnrichedCiqualDatabase database) {
        this.database = database;
    }

    @Tool(description = "Search the CIQUAL French nutritional database for a food item and return " +
            "its name and macronutrients (carbohydrates, fat, protein) per 100g. " +
            "Supports French and English names, fuzzy matching, and synonyms.")
    public List<FoodMacroResult> searchFood(
            @ToolParam(description = "Food name to search for (e.g. 'chicken breast', 'poulet', 'pain')")
            String query) {
        return database.semanticSearch(query, 5, 0.1).stream()
                .map(result -> {
                    var aliment = result.aliment();
                    var macros = aliment.getMacros();
                    return new FoodMacroResult(
                            aliment.getNameFr(),
                            aliment.getNameEn(),
                            macros.carbohydrates(),
                            macros.fat(),
                            macros.protein(),
                            macros.getCalories()
                    );
                })
                .toList();
    }
}
