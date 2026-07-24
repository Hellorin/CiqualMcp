package io.hellorin.ciqual.testsupport;

import io.hellorin.ciqual.database.model.AlimentGroup;
import io.hellorin.ciqual.database.model.Constituent;
import io.hellorin.ciqual.database.model.EnrichedAliment;
import io.hellorin.ciqual.database.model.NutritionalValue;

import java.util.Map;
import java.util.Optional;

/**
 * Shared builders for {@link EnrichedAliment} test data, avoiding XML parsing in unit tests.
 */
public final class AlimentFixtures {

    private AlimentFixtures() {
    }

    public static AlimentGroup group(String groupNameEn, String subgroupNameEn) {
        return new AlimentGroup(
            "01", groupNameEn + " (fr)", groupNameEn,
            "0101", subgroupNameEn + " (fr)", subgroupNameEn,
            Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    public static NutritionalValue nutrient(String code, double value) {
        Constituent constituent = new Constituent(code, "nom-" + code, "name-" + code, Optional.empty());
        return new NutritionalValue(constituent, Optional.of(value), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static NutritionalValue nutrient(String code, String nameFr, String nameEn, double value) {
        Constituent constituent = new Constituent(code, nameFr, nameEn, Optional.empty());
        return new NutritionalValue(constituent, Optional.of(value), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static EnrichedAliment aliment(String code, String nameFr, String nameEn, AlimentGroup group, Map<String, NutritionalValue> nutrients) {
        return new EnrichedAliment(code, nameFr, nameEn, Optional.empty(), 1.0, group, nutrients);
    }
}
