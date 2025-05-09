# Memory Server

**Qdrant × Quarkus MCP Memory Server**  
High-performance semantic memory orchestration for agents and daemons.

---

## Overview

Memory Server provides a powerful semantic memory storage and retrieval system tailored specifically for use with the Model Context Protocol (MCP). It leverages the speed of native Quarkus applications, Kotlin for type-safety, and Qdrant for scalable vector-based semantic search.

---

## Features

- **Semantic Vector Memory:** Store and recall memories based on semantic similarity rather than exact matches.
- **Tag-based Filtering:** Easily organize and filter memories using flexible tagging.
- **Dual CLI and Agent Interface:** Designed for both human operators (`memory-cli`) and AI agents (`memory-proxy`).
- **MCP Compatible:** Uses standard MCP calls for seamless integration with AI agents like Goose.

---

## Quickstart

### Local Development

To run Memory Server locally using Docker Compose:

```bash
docker-compose up
```

### CLI Usage

To use the command-line client for memory operations:

```bash
./memory-cli remember --content "This is a test memory" --tags "qa:true,env:dev"
./memory-cli recall --content "test memory"
```

### Agent (Goose) Integration

Load the memory-server extension via Goose CLI with STDIO proxy:

```bash
/extension memory-proxy
```

Example MCP calls via Goose:

```json
{
  "request": {
    "memory": {
      "content": "Semantic search test"
    },
    "page": 1,
    "pageSize": 10
  }
}
```

---

## Architecture

```
memory-cli ↔ memory-proxy ↔ REST (memory-server) ↔ gRPC ↔ Qdrant
```

- **`memory-server`**: REST endpoint, semantic embedding generation, gRPC client to Qdrant.
- **`memory-cli`**: User/operator CLI.
- **`memory-proxy`**: STDIO proxy enabling MCP tool calls from AI agents like Goose.

---

## Recipes & Usage

- QA Recipes available in [`recipes/`](recipes/).
- Example configuration and recipes provided for immediate integration with Goose CLI.

---

## Contributing

Contributions are welcome! Please open an issue or pull request on GitHub.

---

## License

This project is licensed under the APACHE 2.0 License – see the [LICENSE](./LICENSE) file for details.
