# Chronos

> A temporal distributed-systems debugger that captures immutable event histories, reconstructs causal failures, and deterministically replays counterfactual executions.

**Chronos lets engineers investigate what happened, understand why it happened, and experimentally determine what would have happened if the failure had never occurred.**

## The Vision

Modern distributed systems are incredibly difficult to debug because failures cascade across multiple asynchronous boundaries. Traditional monitoring tells you *that* a system failed. Distributed tracing tells you *where* it failed.

Chronos answers the ultimate debugging questions:
- **WHAT happened?** (Temporal Event History)
- **WHERE did it happen?** (Distributed Event Capture)
- **WHY did it happen?** (Causal Graph Reconstruction)
- **CAN I deliberately cause it?** (Failure Injection)
- **WHAT WOULD HAVE HAPPENED OTHERWISE?** (Counterfactual Replay)

## Core Architecture

Chronos observes your distributed system by capturing events from Kafka. It stores them immutably in PostgreSQL as the absolute source of truth, and asynchronously builds a Causal Graph in Neo4j to explain relationships. 

When a failure occurs, Chronos allows you to fork the historical timeline, remove the variable that caused the failure, and execute an isolated, deterministic replay to observe the counterfactual outcome.

```text
                FACTS
                 │
                 ▼
            MAIN TIMELINE
                 │
              FORK
                 │
                 ▼
        ┌──────────────────┐
        │ Historical State │
        └────────┬─────────┘
                 │
        + Experiment Rules
                 │
                 ▼
        COUNTERFACTUAL WORLD
                 │
               REPLAY
                 │
                 ▼
          NEW EVENTS
                 │
                 ▼
             NEW STATE
                 │
                 ▼
              COMPARE
```

## Technical Stack

- **Java 21 + Spring Boot**: Microservices and the core Chronos Engine
- **Apache Kafka**: Event broker and replay isolation boundary
- **PostgreSQL**: Immutable event store and timeline lineage (Source of Truth)
- **Neo4j**: Graph database for causal relationship traversal
- **Next.js + TypeScript + React Flow**: Counterfactual Studio and Causal Graph visualizer

## Key Capabilities

### 1. Deterministic Counterfactuals
Chronos allows you to create nested experiments (`MAIN` → `EXP-001` → `EXP-002`) without ever mutating historical facts. The logical timeline lineage resolver dynamically composes history, allowing identical forks to execute identically down to the final causal topology.

### 2. Resilience and Source of Truth
Chronos is designed such that PostgreSQL is the absolute source of truth. If Neo4j goes down, Chronos continues to capture events normally. When Neo4j recovers, the entire graph can be deterministically rebuilt from the immutable facts stored in PostgreSQL via the `/api/causality/rebuild` endpoint.

### 3. Fault Injection
Inject `LATENCY`, `DROP`, `DUPLICATE`, or `CRASH` faults directly into the execution flow to deliberately trigger and observe cascading failures.

### 4. Counterfactual Studio
A visual interface that compares the real execution (e.g., `FAILED`) against the counterfactual execution (e.g., `COMPLETED`), clearly highlighting the point of divergence, the removed cause, and the deterministic evidence.

## Getting Started

*(Instructions for running the Docker Compose cluster and starting the microservices)*
