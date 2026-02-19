package io.hellorin.ciqual.database.model;

import java.util.Optional;

/**
 * Represents a food group hierarchy (group, subgroup, sub-subgroup) in the Ciqual database.
 */
public record AlimentGroup(
    String groupCode,
    String groupNameFr,
    String groupNameEn,
    String subgroupCode,
    String subgroupNameFr,
    String subgroupNameEn,
    Optional<String> subSubgroupCode,
    Optional<String> subSubgroupNameFr,
    Optional<String> subSubgroupNameEn
) {

    /**
     * Creates an AlimentGroup with all required fields.
     */
    public AlimentGroup {
        if (groupCode == null || groupCode.isBlank()) {
            throw new IllegalArgumentException("Group code cannot be null or blank");
        }
        if (groupNameFr == null || groupNameFr.isBlank()) {
            throw new IllegalArgumentException("Group French name cannot be null or blank");
        }
        if (groupNameEn == null || groupNameEn.isBlank()) {
            throw new IllegalArgumentException("Group English name cannot be null or blank");
        }
        if (subgroupCode == null || subgroupCode.isBlank()) {
            throw new IllegalArgumentException("Subgroup code cannot be null or blank");
        }
        if (subgroupNameFr == null || subgroupNameFr.isBlank()) {
            throw new IllegalArgumentException("Subgroup French name cannot be null or blank");
        }
        if (subgroupNameEn == null || subgroupNameEn.isBlank()) {
            throw new IllegalArgumentException("Subgroup English name cannot be null or blank");
        }
    }

    /**
     * Creates an AlimentGroup from XML field values.
     */
    public static AlimentGroup fromXml(String groupeCode, String groupeNomFr, String groupeNomEng,
                                       String sousGroupeCode, String sousGroupeNomFr, String sousGroupeNomEng,
                                       String sousSousGroupeCode, String sousSousGroupeNomFr, String sousSousGroupeNomEng) {
        return new AlimentGroup(
            groupeCode.trim(),
            groupeNomFr.trim(),
            groupeNomEng.trim(),
            sousGroupeCode.trim(),
            sousGroupeNomFr.trim(),
            sousGroupeNomEng.trim(),
            parseOptionalCode(sousSousGroupeCode),
            parseOptionalName(sousSousGroupeNomFr),
            parseOptionalName(sousSousGroupeNomEng)
        );
    }

    private static Optional<String> parseOptionalCode(String value) {
        if (value == null || value.isBlank() || value.trim().equals("000000")) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }

    private static Optional<String> parseOptionalName(String value) {
        if (value == null || value.isBlank() || value.trim().equals("-")) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }
}
