# AGENTS.md - Chemlab Backend Development Guide

## Project Overview
**Chemlab** is an interactive periodic table and chemistry education platform built with **Spring Boot 4.x** (for the core backend) and **FastAPI** (for specialized services), **Java 21**, and **MongoDB**. It enables users to explore elements, conduct experiments, create flashcards, and take auto-generated quizzes.

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
