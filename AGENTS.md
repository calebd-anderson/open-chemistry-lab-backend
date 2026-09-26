# AGENTS.md - Chemlab Backend Development Guide

## Project Overview
**Chemlab** is an interactive periodic table and chemistry education platform built with **Spring Boot 4.x** (for the core backend) and **FastAPI** (for specialized services), **Java 25**, and **MongoDB**. It enables users to explore elements, conduct experiments, create flashcards, and take auto-generated quizzes.

### Core Technology Stack
- **Framework**: Spring Boot 4.x (Core Backend), FastAPI (Specialized Services)
- **Language**: Java 25 LTS, Python 3.11+
- **Database**: MongoDB 7.0.40 (via Docker or local)
- **Build**: Maven (Java), Poetry/Pip (Python)
- **Authentication**: JWT (Auth0 library) + BCrypt + Spring Security
- **File Storage**: Azure Blob Storage (prod) / Local filesystem (dev/test)
- **Testing**: JUnit 5 + Mockito + Testcontainers (Java), Pytest (Python)
- **Secrets**: sops + age encryption for `application-prod.enc.yml`

---

## Architecture Patterns

### Hybrid Architecture
The codebase follows a **hybrid architecture**: A **Spring Boot core** for the main domain and **FastAPI services** for specialized, high-performance, or AI-driven tasks.

#### Core Backend (Spring Boot)
The Spring Boot application follows a layered architecture, organized by domain features:

```
controller/api/{domain}/         → HTTP endpoints, request/response handling
    ├── chemistry/               → Element, Reaction APIs
    ├── game/                    → Quiz, Flashcard APIs
    └── user/                    → Auth, Profile APIs
    
service/{domain}/               → Business logic, orchestration
    ├── ElementServiceImpl        → Implements domain.chemistry.ElementService
    ├── ReactionServiceImpl       → Implements domain.chemistry.ReactionService
    └── UserServiceImpl           → User management
    
repository/{domain}/            → Data access layer
    ├── ElementRepository        → Interface (Spring Data MongoDB)
    ├── ElementRepoImpl           → Custom query implementation
    └── ReactionRepository       → MongoDB queries
    
domain/{domain}/                → Service interfaces (contracts)
    └── ServiceInterface<T>      → Generic interface with isValid(T) method
    
model/{domain}/                 → MongoDB entities (@Document)
    ├── Element                  → Chemistry elements
    ├── Reaction                 → Chemical reactions
    └── User                     → User accounts, profiles
    
infrastructure/                 → External integrations
    ├── azure/AzureBlobStorage   → Azure Storage SDK
    ├── pubchem/                 → PubChem API client
    ├── email/                   → Mail service
    └── robohash/                → Avatar generation
```

#### Specialized Services (FastAPI)
Python-based services reside in the `services/` directory, handling data processing, AI/ML workloads, and specialized chemistry computations.

```
services/
    ├── element-analyzer/        → FastAPI service for advanced element analysis
    └── simulation-engine/       → FastAPI service for chemical reaction simulations
```

### Domain Separation
- **Chemistry (Spring)**: Elements (periodic table), Reactions (compound discovery)
- **Game (Spring)**: Quiz service, Flashcard management
- **User (Spring)**: Authentication, Account management, File uploads
- **Specialized (FastAPI)**: Computational chemistry, AI integration, Data heavy lifting

---

## Git Commit Policy

Never create a Git commit automatically.

A commit may be created only if the user explicitly says to commit the current changes in the same conversation. A general request such as “finish this feature” or “prepare the changes” is not permission to commit.

Before any permitted commit:

1. Show the proposed commit message.
2. Show the staged diff.
3. Ask for confirmation.
4. Commit only the changes covered by that confirmation.

Never amend existing commits, force-push, rebase, reset, or push without separate explicit permission.

## Change-Scope Rules
- Modify only files necessary to complete the request.
- Do not discard, overwrite, or revert pre-existing user changes.
- Before editing, inspect the current working tree.
- At the end, report the files changed and the validation commands run.

## Validation
Run the relevant tests, linters, and formatters after making changes.
Do not use validation commands that modify Git history or publish changes.

### Spring Boot Validation
- `./mvnw clean compile`

## File-Editing Rules

Before editing an existing file, always read the relevant current content from disk in the same turn. Do not construct replacement text from memory, an earlier tool result, or assumed formatting.

When making an edit:

- Preserve existing indentation, whitespace, line endings, and formatting.
- Use a short, unique match for exact-text replacements.
- Make the smallest change necessary.
- Re-read the file after editing and verify the intended change.
- Do not overwrite unrelated user changes.

### Recovery from Failed Edits

If an exact-text replacement fails with an error such as
`String to replace not found in file.`:

1. Stop and do not retry the stale replacement.
2. Re-read the target file from disk.
3. Locate the exact current text, including whitespace and indentation.
4. Make the smallest possible edit.
5. Re-read and verify the file afterward.
6. After two failed attempts, stop and report the problem.

---

## Roadmap: Molecule Similarity & Cluster Visualization

The current implementation uses **FastAPI + RDKit** to generate molecular fingerprints (vectors) and **Java (Smile/DMSCAN)** to cluster them, visualizing the result via **D3.js**.

### Current Limitations
1.  **Data Sparsity**: Using only the `fastformula` endpoint restricts the dataset to compounds with the same elemental formula, limiting the richness of the cluster.
2.  **Visualization Bottleneck**: The D3.js force graph currently displays a "star" pattern (one central node connected to all others), failing to show the true topological structure of the clusters.

### Phase 1: Improved Data Ingestion (The "Discovery" Workflow)
**Goal**: Increase dataset diversity and density.
- [ ] **Expand API Usage**: Move beyond `fastformula`. Implement a workflow using `fastsimilarity_2d` or `substructure` searches to find related molecules across different formulas.
- [ ] **Batch Processing**: Implement a Python worker that crawls PubChem for a "seed" molecule, gathering its neighbors and their properties, creating a more robust training/clustering set.
- [ ] **Property Enrichment**: Use the `property` endpoint to fetch more descriptive features (TPSA, LogP, H-Bond counts) to enrich the RDKit fingerprinting process.

### Phase 2: Advanced Molecule Visualization
**Goal**: Transition from a "star" graph to a "cluster" graph.
- [ ] **Link Topology**: Instead of connecting all nodes to a central hub, modify the D3.js implementation to draw links *between* nodes in the 
same cluster (intra-cluster edges).
- [ ] **Hierarchical Clustering**: Implement a tree-based visualization (e.g., Dendrogram) to show how small clusters merge into larger ones.
- [ ] **Interactive Nodes**: Enable clicking a node to "pivot" the view, re-running the clustering logic around the new seed molecule.

### Phase 3: Feature Expansion
- [ ] **SAR Dashboard**: Add a UI component to compare the structural fingerprints of two selected nodes.
- [ ] **Real-time Simulation**: Use the `simulation-engine` (FastAPI) to predict 3D conformer changes and visualize them in the browser.
