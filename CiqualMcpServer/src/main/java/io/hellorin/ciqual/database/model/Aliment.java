package io.hellorin.ciqual.database.model;

import java.util.Optional;

/**
 * Represents a food item (aliment) in the Ciqual database.
 */
public record Aliment(
    String code,
    String nameFr,
    String nameEn,
    Optional<String> nameSci,
    String groupCode,
    String subgroupCode,
    String subSubgroupCode,
    double jonesFactor
) {

    /**
     * Creates an Aliment with all required fields.
     */
    public Aliment {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be null or blank");
        }
        if (nameFr == null || nameFr.isBlank()) {
            throw new IllegalArgumentException("French name cannot be null or blank");
        }
        if (nameEn == null || nameEn.isBlank()) {
            throw new IllegalArgumentException("English name cannot be null or blank");
        }
        if (groupCode == null || groupCode.isBlank()) {
            throw new IllegalArgumentException("Group code cannot be null or blank");
        }
        if (subgroupCode == null || subgroupCode.isBlank()) {
            throw new IllegalArgumentException("Subgroup code cannot be null or blank");
        }
        if (subSubgroupCode == null || subSubgroupCode.isBlank()) {
            throw new IllegalArgumentException("Sub-subgroup code cannot be null or blank");
        }
    }

    /**
     * Creates an Aliment from XML field values.
     */
    public static Aliment fromXml(String code, String nomFr, String nomEng, String nomSci,
                                  String groupeCode, String sousGroupeCode,
                                  String sousSousGroupeCode, String facteurJones) {
        return new Aliment(
            code.trim(),
            nomFr.trim(),
            nomEng.trim(),
            Optional.ofNullable(nomSci).filter(s -> !s.isBlank() && !s.equals(" ")).map(String::trim),
            groupeCode.trim(),
            sousGroupeCode.trim(),
            sousSousGroupeCode.trim(),
            Double.parseDouble(facteurJones.trim())
        );
    }
}
