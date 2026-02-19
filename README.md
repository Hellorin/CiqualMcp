# CiqualMcp

A small experimental project exploring two things at once:

1. **MCP (Model Context Protocol) with Spring AI** — exposing and consuming tools via the MCP standard using Spring AI's server/client starters.
2. **AI-powered search over the CIQUAL database** — querying the French official food composition database through natural language.

---

## What is CIQUAL?

[CIQUAL](https://ciqual.anses.fr/) is the French food composition database published by ANSES. It contains nutritional data (macronutrients, vitamins, minerals, energy, etc.) for thousands of food items, per 100g. This project bundles the 2025 release of CIQUAL as XML files and parses them into an in-memory database.

---

## Architecture

The project is a Maven multi-module application with two Spring Boot services:

```
CiqualMcp/
├── CiqualMcpServer/   ← MCP server exposing food search tools (port 8080)
└── CiqualMcpClient/   ← MCP client + web chat interface (port 8081)
```

### Server (`CiqualMcpServer`)

- Parses the CIQUAL XML files at startup into an in-memory enriched database.
- Exposes a `searchFood` tool via MCP using `spring-ai-starter-mcp-server-webmvc`.
- The tool performs semantic/fuzzy search over food names (French, English, scientific) and food groups, returning macronutrient data (carbs, fat, protein, calories per 100g).

### Client (`CiqualMcpClient`)

- Connects to the MCP server via SSE using `spring-ai-starter-mcp-client`.
- Uses OpenAI (`gpt-4o-mini`) through Spring AI's `ChatClient`.
- Provides a REST endpoint (`POST /api/chat`) and a simple web UI for chatting with a nutritional assistant.
- When the user asks a food question, the LLM automatically calls the `searchFood` tool on the MCP server to fetch real CIQUAL data and incorporates it into its answer.

### Data flow

```
User (browser)
  └─► ChatController (port 8081)
        └─► Spring AI ChatClient → OpenAI gpt-4o-mini
              └─► [tool call] MCP Client
                    └─► MCP Server (port 8080)
                          └─► FoodSearchTools → EnrichedCiqualDatabase
                                └─► SemanticFoodSearch (fuzzy + synonym matching)
                                      └─► CIQUAL XML data
```

---

## Key implementation details

- **Semantic search**: fuzzy matching with Levenshtein distance + synonym expansion (French and English food terms) + weighted multi-field scoring across food name, English name, scientific name, and group hierarchy.
- **Nutrient extraction**: macronutrients (carbs, fat, protein, fiber, alcohol), energy (kcal/kJ), vitamins, minerals, fatty acids — all identified by CIQUAL constituent codes.
- **Calorie estimate**: computed from macros using the 4-4-9-7 rule (carbs/protein/fat/alcohol kcal per gram).
- **MCP transport**: synchronous SSE-based MCP, wired entirely through Spring AI configuration with minimal boilerplate.

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.1 |
| AI integration | Spring AI 1.0.0 |
| Protocol | Model Context Protocol (MCP) |
| LLM | OpenAI gpt-4o-mini |
| Data | CIQUAL 2025 (XML) |
| Build | Maven |

---

## Running locally

### Prerequisites

- Java 21+
- Maven
- An OpenAI API key

### Start the MCP server

```bash
cd CiqualMcpServer
mvn spring-boot:run
```

The server starts on port 8080. It will take a moment to parse the CIQUAL XML files (the composition file is ~67 MB).

### Start the MCP client

```bash
cd CiqualMcpClient
OPENAI_API_KEY=sk-... mvn spring-boot:run
```

The client starts on port 8081.

### Use the chat UI

Open [http://localhost:8081](http://localhost:8081) and ask something like:

- *"What are the macronutrients in chicken breast?"*
- *"How many calories in 100g of brie?"*
- *"Compare the protein content of lentils and beef."*

---

## Project goals

This is intentionally small and exploratory. The main goals were:

- Get hands-on with **Spring AI's MCP support** (server and client starters, tool registration, SSE transport).
- See how well a simple **semantic search** can route natural language food queries to structured CIQUAL data without a vector database.
- Keep everything self-contained — no external database, no embeddings service, just XML files and in-memory search.
