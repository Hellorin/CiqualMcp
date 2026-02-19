package io.hellorin.ciqual.database;

import io.hellorin.ciqual.database.model.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;

/**
 * Parser that builds an enriched Ciqual database with smart data structure.
 */
public class EnrichedCiqualParser {

    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    /**
     * Loads the complete enriched Ciqual database from XML files.
     */
    public static EnrichedCiqualDatabase loadDatabase() throws Exception {
        System.out.println("Loading enriched Ciqual database...");

        // Step 1: Load all base data
        Map<String, Aliment> aliments = parseAliments();
        Map<String, AlimentGroup> groups = parseAlimentGroups();
        Map<String, Constituent> constituents = parseConstituents();
        Map<String, Source> sources = parseSources();

        // Step 2: Load compositions (nutrient values)
        Map<String, List<Composition>> compositionsByAliment = parseCompositions();

        // Step 3: Build enriched aliments
        System.out.print("  Building enriched aliments... ");
        Map<String, EnrichedAliment> enrichedAliments = new HashMap<>();

        for (Aliment aliment : aliments.values()) {
            // Get the aliment's group
            AlimentGroup group = findGroupForAliment(aliment, groups.values());

            // Get all compositions for this aliment
            List<Composition> compositions = compositionsByAliment.getOrDefault(
                aliment.code(), Collections.emptyList()
            );

            // Build nutritional values map
            Map<String, NutritionalValue> nutrients = new HashMap<>();
            for (Composition comp : compositions) {
                Constituent constituent = constituents.get(comp.constituentCode());
                if (constituent != null) {
                    NutritionalValue nutritionalValue = new NutritionalValue(
                        constituent,
                        comp.teneur(),
                        comp.min(),
                        comp.max(),
                        comp.codeConfiance(),
                        comp.sourceCode()
                    );
                    nutrients.put(constituent.code(), nutritionalValue);
                }
            }

            // Create enriched aliment
            EnrichedAliment enriched = new EnrichedAliment(
                aliment.code(),
                aliment.nameFr(),
                aliment.nameEn(),
                aliment.nameSci(),
                aliment.jonesFactor(),
                group,
                nutrients
            );

            enrichedAliments.put(aliment.code(), enriched);
        }

        System.out.println(enrichedAliments.size() + " built");

        EnrichedCiqualDatabase database = new EnrichedCiqualDatabase(
            enrichedAliments, groups, sources
        );

        System.out.println("Database loaded: " + database);
        return database;
    }

    private static AlimentGroup findGroupForAliment(Aliment aliment, Collection<AlimentGroup> groups) {
        return groups.stream()
            .filter(g -> g.groupCode().equals(aliment.groupCode()) &&
                        g.subgroupCode().equals(aliment.subgroupCode()) &&
                        g.subSubgroupCode()
                            .orElse("000000")
                            .equals(aliment.subSubgroupCode()))
            .findFirst()
            .orElse(createDefaultGroup(aliment));
    }

    private static AlimentGroup createDefaultGroup(Aliment aliment) {
        return new AlimentGroup(
            aliment.groupCode(),
            "Unknown",
            "Unknown",
            aliment.subgroupCode(),
            "Unknown",
            "Unknown",
            Optional.of(aliment.subSubgroupCode()),
            Optional.of("Unknown"),
            Optional.of("Unknown")
        );
    }

    private static Map<String, Aliment> parseAliments() throws Exception {
        System.out.print("  Loading aliments... ");
        Map<String, Aliment> aliments = new HashMap<>();

        try (InputStream is = EnrichedCiqualParser.class.getResourceAsStream("/ciqual/alim_2025_11_03.xml")) {
            if (is == null) throw new IllegalStateException("alim_2025_11_03.xml not found");

            XMLEventReader reader = xmlInputFactory.createXMLEventReader(is);
            Map<String, String> currentElement = new HashMap<>();

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();

                    if (elementName.equals("ALIM")) {
                        currentElement.clear();
                    } else if (!elementName.equals("TABLE")) {
                        String content = reader.getElementText().trim();
                        currentElement.put(elementName, content);
                    }
                } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("ALIM")) {
                    Aliment aliment = Aliment.fromXml(
                        currentElement.get("alim_code"),
                        currentElement.get("alim_nom_fr"),
                        currentElement.get("alim_nom_eng"),
                        currentElement.get("alim_nom_sci"),
                        currentElement.get("alim_grp_code"),
                        currentElement.get("alim_ssgrp_code"),
                        currentElement.get("alim_ssssgrp_code"),
                        currentElement.get("facteur_Jones")
                    );
                    aliments.put(aliment.code(), aliment);
                }
            }
            reader.close();
        }
        System.out.println(aliments.size() + " loaded");
        return aliments;
    }

    private static Map<String, AlimentGroup> parseAlimentGroups() throws Exception {
        System.out.print("  Loading aliment groups... ");
        Map<String, AlimentGroup> groups = new HashMap<>();

        try (InputStream is = EnrichedCiqualParser.class.getResourceAsStream("/ciqual/alim_grp_2025_11_03.xml")) {
            if (is == null) throw new IllegalStateException("alim_grp_2025_11_03.xml not found");

            XMLEventReader reader = xmlInputFactory.createXMLEventReader(is);
            Map<String, String> currentElement = new HashMap<>();

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();

                    if (elementName.equals("ALIM_GRP")) {
                        currentElement.clear();
                    } else if (!elementName.equals("TABLE")) {
                        String content = reader.getElementText().trim();
                        currentElement.put(elementName, content);
                    }
                } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("ALIM_GRP")) {
                    AlimentGroup group = AlimentGroup.fromXml(
                        currentElement.get("alim_grp_code"),
                        currentElement.get("alim_grp_nom_fr"),
                        currentElement.get("alim_grp_nom_eng"),
                        currentElement.get("alim_ssgrp_code"),
                        currentElement.get("alim_ssgrp_nom_fr"),
                        currentElement.get("alim_ssgrp_nom_eng"),
                        currentElement.get("alim_ssssgrp_code"),
                        currentElement.get("alim_ssssgrp_nom_fr"),
                        currentElement.get("alim_ssssgrp_nom_eng")
                    );
                    String key = group.groupCode() + "-" + group.subgroupCode() + "-" +
                                group.subSubgroupCode().orElse("000000");
                    groups.put(key, group);
                }
            }
            reader.close();
        }
        System.out.println(groups.size() + " loaded");
        return groups;
    }

    private static Map<String, Constituent> parseConstituents() throws Exception {
        System.out.print("  Loading constituents... ");
        Map<String, Constituent> constituents = new HashMap<>();

        try (InputStream is = EnrichedCiqualParser.class.getResourceAsStream("/ciqual/const_2025_11_03.xml")) {
            if (is == null) throw new IllegalStateException("const_2025_11_03.xml not found");

            XMLEventReader reader = xmlInputFactory.createXMLEventReader(is);
            Map<String, String> currentElement = new HashMap<>();

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();

                    if (elementName.equals("CONST")) {
                        currentElement.clear();
                    } else if (!elementName.equals("TABLE")) {
                        String content = reader.getElementText().trim();
                        currentElement.put(elementName, content);
                    }
                } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("CONST")) {
                    Constituent constituent = Constituent.fromXml(
                        currentElement.get("const_code"),
                        currentElement.get("const_nom_fr"),
                        currentElement.get("const_nom_eng"),
                        currentElement.get("code_INFOODS")
                    );
                    constituents.put(constituent.code(), constituent);
                }
            }
            reader.close();
        }
        System.out.println(constituents.size() + " loaded");
        return constituents;
    }

    private static Map<String, List<Composition>> parseCompositions() throws Exception {
        System.out.print("  Loading compositions... ");
        Map<String, List<Composition>> compositionsByAliment = new HashMap<>();

        try (InputStream is = EnrichedCiqualParser.class.getResourceAsStream("/ciqual/compo_2025_11_03.xml")) {
            if (is == null) throw new IllegalStateException("compo_2025_11_03.xml not found");

            XMLEventReader reader = xmlInputFactory.createXMLEventReader(is);
            Map<String, String> currentElement = new HashMap<>();

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();

                    if (elementName.equals("COMPO")) {
                        currentElement.clear();
                    } else if (!elementName.equals("TABLE")) {
                        String content = reader.getElementText().trim();
                        currentElement.put(elementName, content);
                    }
                } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("COMPO")) {
                    Composition composition = Composition.fromXml(
                        currentElement.get("alim_code"),
                        currentElement.get("const_code"),
                        currentElement.get("teneur"),
                        currentElement.get("min"),
                        currentElement.get("max"),
                        currentElement.get("code_confiance"),
                        currentElement.get("source_code")
                    );

                    compositionsByAliment
                        .computeIfAbsent(composition.alimentCode(), k -> new ArrayList<>())
                        .add(composition);
                }
            }
            reader.close();
        }

        int totalCompositions = compositionsByAliment.values().stream()
            .mapToInt(List::size)
            .sum();
        System.out.println(totalCompositions + " loaded");
        return compositionsByAliment;
    }

    private static Map<String, Source> parseSources() throws Exception {
        System.out.print("  Loading sources... ");
        Map<String, Source> sources = new HashMap<>();

        try (InputStream is = EnrichedCiqualParser.class.getResourceAsStream("/ciqual/sources_2025_11_03.xml")) {
            if (is == null) throw new IllegalStateException("sources_2025_11_03.xml not found");

            XMLEventReader reader = xmlInputFactory.createXMLEventReader(is);
            Map<String, String> currentElement = new HashMap<>();

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();

                    if (elementName.equals("SOURCES")) {
                        currentElement.clear();
                    } else if (!elementName.equals("TABLE")) {
                        String content = reader.getElementText().trim();
                        currentElement.put(elementName, content);
                    }
                } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("SOURCES")) {
                    Source source = Source.fromXml(
                        currentElement.get("source_code"),
                        currentElement.get("ref_citation")
                    );
                    sources.put(source.code(), source);
                }
            }
            reader.close();
        }
        System.out.println(sources.size() + " loaded");
        return sources;
    }
}
